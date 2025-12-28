package com.movie.movie.service.impl;

import com.movie.movie.convert.EntityToResponse;
import com.movie.movie.entity.MovieGenres;
import com.movie.movie.entity.Movies;
import com.movie.movie.entity.Users;
import com.movie.movie.model.response.MovieResponse;
import com.movie.movie.repository.MoviesRepository;
import com.movie.movie.repository.UsersRepository;
import com.movie.movie.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    private final UsersRepository usersRepository;
    private final MoviesRepository moviesRepository;
    private final EntityToResponse entityToResponse;

    @Override
    public List<MovieResponse> getRecommendedMovies() {
        // 1. Lấy User hiện tại
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Nếu khách vãng lai -> Trả về Trending
        if (username == null || username.equals("anonymousUser")) {
            return getTrendingFallback();
        }

        Users user = usersRepository.findByUsername(username);

        // 2. Lấy danh sách phim đã xem/thích (Để loại trừ)
        Set<Movies> interactedMovies = new HashSet<>();

        // Null check cẩn thận để tránh lỗi NullPointerException
        if (user.getWatchHistories() != null) {
            user.getWatchHistories().forEach(h -> interactedMovies.add(h.getMovie()));
        }
        if (user.getWatchLists() != null) {
            user.getWatchLists().forEach(w -> interactedMovies.add(w.getMovie()));
        }

        List<Long> watchedIds = interactedMovies.stream()
                .map(Movies::getId)
                .collect(Collectors.toList());

        // Thêm ID -1 để tránh lỗi SQL nếu list rỗng
        if (watchedIds.isEmpty()) watchedIds.add(-1L);


        // 3. Phân tích: Tìm Top 3 Thể loại User thích
        Map<Long, Integer> genreCount = new HashMap<>();
        for (Movies movie : interactedMovies) {
            if (movie.getMovieGenres() != null) {
                for (MovieGenres mg : movie.getMovieGenres()) {
                    Long genreId = mg.getGenre().getId().longValue(); // Đảm bảo gọi đúng getter của Genre
                    genreCount.put(genreId, genreCount.getOrDefault(genreId, 0) + 1);
                }
            }
        }

        List<Long> topGenreIds = genreCount.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        if (topGenreIds.isEmpty()) topGenreIds.add(-1L);


        // 👇 4. LOGIC MỚI: Lấy danh sách Diễn viên yêu thích từ field `actorLists`
        List<Long> likedActorIds = new ArrayList<>();

        // Dùng đúng tên biến actorLists trong Entity Users của bạn
        if (user.getActorLists() != null) {
            likedActorIds = user.getActorLists().stream()
                    // Giả sử UserActorList có quan hệ với Person (getPerson) và Person có ID
                    .map(ual -> ual.getPerson().getId())
                    .collect(Collectors.toList());
        }

        if (likedActorIds.isEmpty()) {
            likedActorIds.add(-1L); // Tránh lỗi SQL
        }


        // 5. Gọi Repository (Tìm phim theo Thể loại HOẶC Diễn viên)
        List<Movies> recommendedMovies = moviesRepository.findMoviesByGenresOrActorsAndNotWatched(
                topGenreIds,
                likedActorIds, // Truyền list diễn viên vào đây
                watchedIds,
                PageRequest.of(0, 12) // Lấy 12 phim
        );

        // 6. Nếu kết quả ít quá (< 5 phim), lấy thêm Trending bù vào
        if (recommendedMovies.size() < 5) {
            List<Movies> trending = moviesRepository.findTop10ByOrderByViewCountDesc();
            for (Movies m : trending) {
                // Chỉ thêm nếu chưa xem và chưa có trong list recommend
                if (!watchedIds.contains(m.getId()) &&
                        recommendedMovies.stream().noneMatch(rm -> rm.getId().equals(m.getId()))) {
                    recommendedMovies.add(m);
                }
            }
        }

        // 7. Convert sang Response
        return recommendedMovies.stream()
                .map(entityToResponse::convertFromMovie)
                .collect(Collectors.toList());
    }

    private List<MovieResponse> getTrendingFallback() {
        return moviesRepository.findTop10ByOrderByViewCountDesc().stream()
                .map(entityToResponse::convertFromMovie)
                .collect(Collectors.toList());
    }
}
