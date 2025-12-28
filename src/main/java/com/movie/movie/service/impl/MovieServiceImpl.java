package com.movie.movie.service.impl;

import com.movie.movie.convert.EntityToResponse;
import com.movie.movie.entity.*;
import com.movie.movie.model.response.*;
import com.movie.movie.repository.*;
import com.movie.movie.service.MovieService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {
    @Autowired
    private MoviesRepository moviesRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private CountriesRepository countriesRepository;

    @Autowired
    private PersonsRepository personsRepository;

    @Autowired
    private EpisodeRepsitory episodeRepository;

    @Autowired
    private GenresRepository genresRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EntityToResponse entityToResponse;
    @Autowired
    private WatchHistoryRepository watchHistoryRepository;
    @Autowired
    private SeasonsRepository seasonsRepository;

    @Override
    public List<MovieResponse> getMovie(String text) {
        List<Movies> moviesList = moviesRepository.findByTitleContaining(text);
        List<MovieResponse> movieResponseList = new ArrayList<>();
        for (Movies movie : moviesList) {
            MovieResponse movieResponse = entityToResponse.convertFromMovie(movie);
            movieResponseList.add(movieResponse);
        }

        return movieResponseList;
    }

    @Override
    public List<MovieResponse> getHistory() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = usersRepository.findByUsername(username);

        // Lấy list từ Repository đã sắp xếp DESC
        List<WatchHistory> historyList = watchHistoryRepository.findByUserOrderByLastWatchedAtDesc(user);

        List<MovieResponse> movieResponseList = new ArrayList<>();
        for (WatchHistory watchHistory : historyList) {
            Movies movies = watchHistory.getMovie();
            MovieResponse movieResponse = entityToResponse.convertFromMovie(movies);
            movieResponseList.add(movieResponse);
        }
        return movieResponseList;
    }

//    @Override
//    public List<MovieResponse> getHistory() {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        Users users = usersRepository.findByUsername(username);
//        List<MovieResponse> movieResponseList = new ArrayList<>();
//        List<WatchHistory> historyList = users.getWatchHistories();
//        for (WatchHistory watchHistory : historyList) {
//            Movies movies = watchHistory.getMovie();
//            MovieResponse movieResponse = entityToResponse.convertFromMovie(movies);
//            movieResponseList.add(movieResponse);
//        }
//        return movieResponseList;
//    }

    @Override
    public void deleteMovie(Long id) {
        moviesRepository.deleteById(id);
    }

    @Override
    public PersonResponse getPersons(Long id) {
        Movies movies = moviesRepository.findById(id).get();
        List<MovieCast> movieCasts = movies.getCasts();
        List<MovieCrew> movieCrews = movies.getCrews();
        List<CastResponse> castResponses = new ArrayList<>();
        List<DirectorResponse> directorResponses = new ArrayList<>();
        for (MovieCast movieCast : movieCasts) {
            Persons person = movieCast.getPerson();
//            CastResponse castResponse = new CastResponse(person.getId(), person.getFullName(),
//                    person.getSlug(), person.getAvatarUrl(), person.getGender(),person.getDateOfBirth());
            CastResponse castResponse = modelMapper.map(person, CastResponse.class);
            castResponses.add(castResponse);
        }

        for (MovieCrew movieCrew : movieCrews) {
            Persons person = movieCrew.getPerson();
            DirectorResponse directorResponse = new DirectorResponse(person.getId(), person.getFullName(),
                    person.getSlug(), person.getAvatarUrl(), person.getGender(),person.getDateOfBirth());
            directorResponses.add(directorResponse);
        }
        return new PersonResponse(castResponses, directorResponses);
    }

//    @Override
//    @Transactional
//    public void addMovie(MovieResponse movieDTO) {
//        Movies movies;
//
//        // 1. KIỂM TRA: UPDATE HAY CREATE?
//        if (movieDTO.getId() != null && movieDTO.getId() > 0) {
//            // Update: Lấy từ DB lên để Hibernate quản lý
//            movies = moviesRepository.findById(movieDTO.getId())
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phim ID: " + movieDTO.getId()));
//        } else {
//            // Create: Tạo mới
//            movies = new Movies();
//        }
//
//        // 2. MAP THÔNG TIN CƠ BẢN
//        movies.setTitle(movieDTO.getTitle());
//        movies.setDescription(movieDTO.getDescription());
//        movies.setPosterUrl(movieDTO.getPoster());
//        movies.setVideoUrl(movieDTO.getVideoUrl());
//        movies.setSubtTitle(movieDTO.getSubTitle());
//        movies.setRuntime(movieDTO.getDuration());
//        movies.setImdbRating(movieDTO.getImdb());
//        movies.setAgeRating(movieDTO.getRate());
//        movies.setCountry(countriesRepository.findByCountryCode(movieDTO.getCountry()));
//
//        // Xử lý Slug (Chỉ tạo lại nếu là phim mới hoặc slug bị thay đổi)
//        String newSlug = movieDTO.getSlug();
//        if (movies.getId() == null || (newSlug != null && !newSlug.equals(movies.getSlug()))) {
//            if (newSlug == null || newSlug.isEmpty()) {
//                newSlug = movieDTO.getTitle().toLowerCase().replace(" ", "-");
//            }
//            // Check trùng slug
//            String tempSlug = newSlug;
//            int count = 1;
//            while (moviesRepository.existsBySlug(tempSlug) && !tempSlug.equals(movies.getSlug())) {
//                tempSlug = newSlug + "-" + count;
//                count++;
//            }
//            movies.setSlug(tempSlug);
//        }
//
//        // Lưu tạm để có ID (quan trọng cho trường hợp Create)
//        movies = moviesRepository.save(movies);
//
//        // 3. XỬ LÝ GENRES (Dùng clear() + add() thay vì setList)
//        if (movies.getMovieGenres() == null) movies.setMovieGenres(new ArrayList<>());
//        movies.getMovieGenres().clear(); // Xóa sạch cũ
//
//        if (movieDTO.getGenres() != null) {
//            for (String genreSlug : movieDTO.getGenres()) {
//                Genres genres = genresRepository.findByGenreSlug(genreSlug);
//                if (genres != null) {
//                    MovieGenres movieGenre = new MovieGenres();
//                    movieGenre.setGenre(genres);
//                    movieGenre.setMovie(movies);
//                    movies.getMovieGenres().add(movieGenre); // Add vào list quản lý
//                }
//            }
//        }
//
//        // 4. XỬ LÝ CAST (Dùng clear() + add())
//        if (movies.getCasts() == null) movies.setCasts(new ArrayList<>());
//        movies.getCasts().clear(); // Xóa sạch cũ
//
//        if (movieDTO.getCast() != null) {
//            for (CastResponse cast : movieDTO.getCast()) {
//                Persons person = personsRepository.findBySlug(cast.getSlug());
//                if (person == null) {
//                    Persons newPerson = new Persons();
//                    newPerson.setFullName(cast.getName());
//                    newPerson.setSlug(cast.getSlug());
//                    newPerson.setAvatarUrl(cast.getImage());
//                    newPerson.setGender(cast.getGender());
//                    person = personsRepository.save(newPerson);
//                }
//
//                MovieCast movieCast = new MovieCast();
//                movieCast.setPerson(person);
//                movieCast.setMovie(movies);
//                // Có thể set thêm characterName nếu FE có gửi
//                movies.getCasts().add(movieCast); // Add vào list quản lý
//            }
//        }
//
//        // 5. XỬ LÝ SEASON & EPISODES
//        // Tìm Season 1 hiện tại hoặc tạo mới
//        Seasons season;
//        if (movies.getSeasons() != null && !movies.getSeasons().isEmpty()) {
//            season = movies.getSeasons().get(0); // Lấy season đầu tiên
//        } else {
//            season = new Seasons();
//            season.setTitle(movieDTO.getTitle()); // Hoặc "Season 1"
//            season.setSeasonNumber(1);
//            season.setMovie(movies);
//            // Lưu season để có ID trước khi thêm tập
//            season = seasonsRepository.save(season);
//
//            // Khởi tạo list seasons cho movie nếu chưa có
//            if (movies.getSeasons() == null) movies.setSeasons(new ArrayList<>());
//            movies.getSeasons().add(season);
//        }
//
//        // Xử lý Episodes: Xóa cũ -> Thêm mới
//        if (season.getEpisodes() != null) {
//            episodeRepository.deleteAll(season.getEpisodes()); // Xóa trong DB
//            season.getEpisodes().clear(); // Xóa trong List
//        } else {
//            season.setEpisodes(new ArrayList<>());
//        }
//
//        if (movieDTO.getEpisodes() != null) {
//            List<Episodes> newEpisodes = new ArrayList<>();
//            for (EpisodeResponse epDTO : movieDTO.getEpisodes()) {
//                Episodes episode = new Episodes();
//                episode.setTitle(epDTO.getTitle());
//                episode.setVideoUrl(epDTO.getVideoUrl());
//                episode.setSeason(season);
//                episode.setEpisodeNumber(newEpisodes.size() + 1); // Tự động đánh số tập
//                newEpisodes.add(episode);
//            }
//            episodeRepository.saveAll(newEpisodes);
//            season.getEpisodes().addAll(newEpisodes);
//        }
//
//        // Lưu lần cuối
//        moviesRepository.save(movies);
//    }

//    @Override
//    @Transactional // Quan trọng để đảm bảo tính toàn vẹn dữ liệu
//    public void addMovie(MovieResponse movie) {
//        Movies movies = new Movies();
//        // 1. Map thông tin cơ bản
//        movies.setId(movie.getId());
//        movies.setTitle(movie.getTitle());
//        movies.setSlug(movie.getSlug());
//        movies.setDescription(movie.getDescription());
//        movies.setPosterUrl(movie.getPoster());
//        movies.setVideoUrl(movie.getVideoUrl());
//        movies.setSubtTitle(movie.getSubTitle());
//        movies.setRuntime(movie.getDuration()); // Đảm bảo DB là String, nếu Int phải parse
//        movies.setImdbRating(movie.getImdb());
//        movies.setAgeRating(movie.getRate());
//
//        // Xử lý Country (Nếu FE gửi mã "my", "vn"...)
//        movies.setCountry(countriesRepository.findByCountryCode(movie.getCountry()));
//
//        // Lưu Movie trước để có ID dùng cho các bảng con (Tùy Cascade type, nhưng save trước cho chắc)
////        movies = moviesRepository.save(movies);
//
//        // 2. Xử lý Genres
//        List<MovieGenres> movieGenres = new ArrayList<>();
//        for(String genreSlug : movie.getGenres()){
//            Genres genres = genresRepository.findByGenreSlug(genreSlug);
//            if (genres != null) {
//                MovieGenres movieGenre = new MovieGenres();
//                movieGenre.setGenre(genres);
//                movieGenre.setMovie(movies);
//                // Lưu bảng trung gian (hoặc add vào list nếu dùng Cascade.ALL)
//                movieGenres.add(movieGenre);
//            }
//        }
//        movies.setMovieGenres(movieGenres);
//
//        // 3. Xử lý Cast (FIX LỖI TRÙNG LẶP & QUÊN ADD)
//        List<MovieCast> listMovieCast = new ArrayList<>();
//        for(CastResponse cast : movie.getCast()){
//            // Tìm diễn viên theo Slug xem có chưa
//            Persons person = personsRepository.findBySlug(cast.getSlug());
//            if (person == null) {
//                Persons newPerson = new Persons();
//                newPerson.setFullName(cast.getName());
//                newPerson.setSlug(cast.getSlug());
//                newPerson.setAvatarUrl(cast.getImage());
//                newPerson.setGender(cast.getGender());
//                person = personsRepository.save(newPerson);
//            }
//
//            MovieCast movieCast = new MovieCast();
//            movieCast.setPerson(person);
//            movieCast.setMovie(movies);
//            // QUAN TRỌNG: Add vào list
//            listMovieCast.add(movieCast);
//        }
//        movies.setCasts(listMovieCast);
//
//        // 4. Xử lý Season & Episode (FIX LỖI THỨ TỰ)
//        // Tạo Season trước
////        Seasons season = new Seasons();
////        season.setTitle("Season 1"); // Mặc định hoặc lấy từ FE
////        season.setSeasonNumber(1);
////        season.setMovie(movies);
////        season = seasonsRepository.save(season); // Save Season để có ID
//
//
//
//        List<Episodes> episodesList = new ArrayList<>();
//
//        if (movie.getEpisodes() != null && !movie.getEpisodes().isEmpty()) {
//            for (EpisodeResponse episode : movie.getEpisodes()) {
//                Episodes episodes = new Episodes();
//                episodes.setTitle(episode.getTitle());
//                episodes.setVideoUrl(episode.getVideoUrl());
//                episodeRepository.save(episodes);
//                episodesList.add(episodes);
//            }
//        }
//
////        Seasons seasons = new Seasons();
////        seasons.setTitle(movie.getTitle());
////        seasons.setMovie(movies);
////        seasons.setSeasonNumber(1);
////        seasons.setEpisodes(episodesList);
////        List<Seasons> seasonsList = new ArrayList<>();
////        seasonsList.add(seasons);
////        movies.setSeasons(seasonsList);
//
//        Seasons seasons = seasonsRepository.findByMovie(movies);
//        if (seasons == null) {
//            seasons = new Seasons();
//            seasons.setTitle(movie.getTitle());
//            seasons.setMovie(movies);
//            seasons.setSeasonNumber(1);
//            seasons.setEpisodes(episodesList);
//            seasons = seasonsRepository.save(seasons);
//        } else {
//            seasons.setEpisodes(episodesList);
//        }
//        List<Seasons> seasonsList = new ArrayList<>();
//        seasonsList.add(seasons);
//        movies.setSeasons(seasonsList);
//        // Nếu là phim bộ (có episodes)
////        if (movie.getEpisodes() != null && !movie.getEpisodes().isEmpty()) {
////            for (EpisodeResponse epDTO : movie.getEpisodes()) {
////                Episodes episode = new Episodes();
////                episode.setTitle(epDTO.getTitle());
////                episode.setVideoUrl(epDTO.getVideoUrl());
////                episode.setSeason(season); // Gán Season cho Episode
////                episodesList.add(episode);
////                // Save episode
////                episodeRepository.save(episode);
////            }
////        }
////        season.setEpisodes(episodesList);
//
//        // Cập nhật lại Movie lần cuối (để lưu các quan hệ List nếu cần)
//        moviesRepository.save(movies);
//    }

    @Override
    public void addMovie(MovieResponse movie) {
        Movies movies = moviesRepository.findById(movie.getId()).get();
        if (movies == null) {
            movies = new Movies();
        }
//        movies.setId(movie.getId());
        movies.setTitle(movie.getTitle());
        movies.setDescription(movie.getDescription());
        movies.setCountry(countriesRepository.findByCountryCode(movie.getCountry()));
        movies.setPosterUrl(movie.getPoster());
        movies.setImdbRating(movie.getImdb());
        movies.setAgeRating(movie.getRate());
        movies.setRuntime(movie.getDuration());
        movies.setSlug(movie.getSlug());
        movies.setSubtTitle(movie.getSubTitle());
        movies.setVideoUrl(movie.getVideoUrl());
        List<MovieCast> listMovieCast = new ArrayList<>();
        for(CastResponse cast : movie.getCast() ){
//            Persons persons = personsRepository.findById(cast.getId()).get();
            Persons persons = new Persons();
            persons.setFullName(cast.getName());
            persons.setSlug(cast.getSlug());
            persons.setAvatarUrl(cast.getImage());
            persons.setGender(cast.getGender());
            personsRepository.save(persons);
            MovieCast movieCast = new MovieCast();
            movieCast.setPerson(persons);
            movieCast.setMovie(movies);
            listMovieCast.add(movieCast);
        }
        movies.setCasts(listMovieCast);
        List<Episodes> episodesList = new ArrayList<>();
        if (movie.getEpisodes() != null) {
            for (EpisodeResponse episode : movie.getEpisodes()) {
                Episodes episodes = new Episodes();
                episodes.setTitle(episode.getTitle());
                episodes.setVideoUrl(episode.getVideoUrl());
                episodeRepository.save(episodes);
                episodesList.add(episodes);
            }
        }
//        for (EpisodeResponse episode : movie.getEpisodes()) {
//            Episodes episodes = new Episodes();
//            episodes.setTitle(episode.getTitle());
//            episodes.setVideoUrl(episode.getVideoUrl());
//            episodeRepository.save(episodes);
//            episodesList.add(episodes);
//        }
        if (movie.getId() == null) {
            Seasons seasons = new Seasons();
            seasons.setTitle("Season 1");
            seasons.setMovie(movies);
            seasons.setSeasonNumber(1);
            seasons.setEpisodes(episodesList);
            List<Seasons> seasonsList = new ArrayList<>();
            seasonsList.add(seasons);
            movies.setSeasons(seasonsList);
        }

        List<MovieGenres> movieGenres = new ArrayList<>();
        for(String genre : movie.getGenres()){
            Genres genres = genresRepository.findByGenreSlug(genre);
            MovieGenres movieGenre = new MovieGenres();
            movieGenre.setGenre(genres);
            movieGenre.setMovie(movies);
            movieGenres.add(movieGenre);
        }
        movies.setMovieGenres(movieGenres);
        moviesRepository.save(movies);

    }

    @Override
    public MovieResponse getMovieDetail(String slug) {
        Movies movie = moviesRepository.findBySlug(slug);

        return entityToResponse.convertFromMovie(movie);
    }

    @Override
    public void increaseViewCount(Long id) {
        if (moviesRepository.existsById(id)) {
            moviesRepository.incrementViewCount(id);
        }
    }

    @Override
    public List<MovieResponse> getTrendingMovies() {
        // 1. Gọi Repo lấy Top 10 view cao nhất
        List<Movies> trendingMovies = moviesRepository.findTop10ByOrderByViewCountDesc();

        // 2. Convert sang DTO (Hàm convert cũ của bạn đã xử lý tốt vụ user login/logout)
        return trendingMovies.stream()
                .map(entityToResponse::convertFromMovie)
                .collect(Collectors.toList());
    }
}
