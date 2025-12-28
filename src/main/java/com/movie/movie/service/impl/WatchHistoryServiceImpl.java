package com.movie.movie.service.impl;

import com.movie.movie.entity.Movies;
import com.movie.movie.entity.Users;
import com.movie.movie.entity.WatchHistory;
import com.movie.movie.repository.MoviesRepository;
import com.movie.movie.repository.UsersRepository;
import com.movie.movie.repository.WatchHistoryRepository;
import com.movie.movie.service.WatchHistoryService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WatchHistoryServiceImpl implements WatchHistoryService {
    @Autowired
    private WatchHistoryRepository watchHistoryRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MoviesRepository moviesRepository;


//    @Override
//    public void addToHistory(Long id) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        Users users = usersRepository.findByUsername(username);
//        Movies movies = moviesRepository.findById(id).get();
//        WatchHistory watchHistory = new WatchHistory();
//        watchHistory.setUser(users);
//        watchHistory.setMovie(movies);
//        watchHistoryRepository.save(watchHistory);
//    }
    @Override
    @Transactional
    public void addToHistory(Long movieId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = usersRepository.findByUsername(username);

        Movies movie = moviesRepository.findById(movieId).get();

        System.out.println("hh");
        // 1. Kiểm tra tồn tại
        WatchHistory historyItem = watchHistoryRepository.findByUserAndMovie(user, movie)
                .orElse(null);

        if (historyItem == null) {
            // 2. Chưa có -> Tạo mới
            historyItem = new WatchHistory();
            historyItem.setUser(user);
            historyItem.setMovie(movie);
        }

        // 3. Cập nhật thời gian xem mới nhất
        // (Nếu dùng @UpdateTimestamp ở entity thì chỉ cần save là nó tự nhảy time)
        historyItem.setLastWatchedAt(LocalDateTime.now());

        watchHistoryRepository.save(historyItem);
    }
}
