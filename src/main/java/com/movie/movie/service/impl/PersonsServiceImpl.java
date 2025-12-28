package com.movie.movie.service.impl;

import com.movie.movie.convert.EntityToResponse;
import com.movie.movie.entity.*;
import com.movie.movie.model.response.CastResponse;
import com.movie.movie.model.response.MovieResponse;
import com.movie.movie.model.response.PersonDetailResponse;
import com.movie.movie.repository.PersonsRepository;
import com.movie.movie.repository.UserActorListRepository;
import com.movie.movie.repository.UsersRepository;
import com.movie.movie.service.PersonsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonsServiceImpl implements PersonsService {

    @Autowired
    private PersonsRepository personsRepository;

    @Autowired
    private EntityToResponse entityToResponse;
    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private UserActorListRepository userActorListRepository;

    @Override
    public List<CastResponse> getCast(String text) {
        List<Persons> persons = personsRepository.findByFullNameContaining(text);
        List<CastResponse> castResponses = new ArrayList<>();
        for (Persons person : persons) {
            CastResponse castResponse = new CastResponse();
            castResponse.setId(person.getId());
            castResponse.setDob(person.getDateOfBirth());
            castResponse.setSlug(person.getSlug());
            castResponse.setName(person.getFullName());
            castResponse.setImage(person.getAvatarUrl());
            castResponse.setGender(person.getGender());
            castResponses.add(castResponse);
        }
        return castResponses;
    }

    @Override
    public void deleteCast(Long id) {
        personsRepository.deleteById(id);
    }

    @Override
    public List<MovieResponse> getMoviesOfCast(Long id) {
        Persons persons = personsRepository.findById(id).get();
        List<MovieCast> movieCasts = persons.getMovieCasts();
        List<MovieResponse> movieResponses = new ArrayList<>();
        for (MovieCast movieCast : movieCasts) {
            Movies movies = movieCast.getMovie();
            MovieResponse movieResponse = entityToResponse.convertFromMovie(movies);
            movieResponses.add(movieResponse);
        }
        return movieResponses;
    }

    @Override
    public PersonDetailResponse getPersonDetail(String slug) {
        // 1. Tìm diễn viên theo slug
        Persons person = personsRepository.findBySlug(slug);

        // 2. Lấy User hiện tại để check "Yêu thích"
        Users currentUser = null;
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (username != null && !username.equals("anonymousUser")) {
            currentUser = usersRepository.findByUsername(username);
        }

        // 3. Map sang Response
        PersonDetailResponse response = new PersonDetailResponse();
        response.setId(person.getId());
        response.setName(person.getFullName());
        response.setSlug(person.getSlug());
        response.setImage(person.getAvatarUrl());
        response.setBio(person.getBiography());
        response.setGender(person.getGender());
        response.setDob(person.getDateOfBirth());

        // 4. Check isActorLove
        if (currentUser != null) {
            boolean isLoved = userActorListRepository.existsByUserAndPerson(currentUser, person);
            response.setActorLove(isLoved);
        }

        // 5. 👇 LẤY DANH SÁCH PHIM (Từ bảng MovieCast)
        List<MovieResponse> movieResponses = new ArrayList<>();
        if (person.getMovieCasts() != null) {
            for (MovieCast cast : person.getMovieCasts()) {
                Movies movie = cast.getMovie();
                // Dùng lại hàm convert cũ của bạn để convert phim
                // Lưu ý: Hàm này cũng sẽ tự động check isMovieLove cho từng phim luôn!
                MovieResponse movieDto = entityToResponse.convertFromMovie(movie);
                movieResponses.add(movieDto);
            }
        }
        response.setParticipatedMovies(movieResponses);

        return response;
    }
}
