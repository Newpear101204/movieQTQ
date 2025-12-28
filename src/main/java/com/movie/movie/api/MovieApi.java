package com.movie.movie.api;

import com.movie.movie.entity.Genres;
import com.movie.movie.model.dto.MovieDTO;
import com.movie.movie.model.dto.PersonDTO;
import com.movie.movie.model.request.SearchMovieRequest;
import com.movie.movie.model.response.MovieResponse;
import com.movie.movie.model.response.PersonResponse;
import com.movie.movie.model.response.SearchResponse;
import com.movie.movie.service.GenresService;
import com.movie.movie.service.MovieService;
import com.movie.movie.service.PersonsService;
import com.movie.movie.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movie/")
@CrossOrigin(origins = "http://localhost:3000")
public class MovieApi {
    @Autowired
    private GenresService genresService;

    @Autowired
    private MovieService movieService;

    @Autowired
    private PersonsService personService;

    @Autowired
    private PersonsService personsService;
    @Autowired
    private RecommendService recommendService;


    //api xử lí thanh tìm kiếm
    @PostMapping("/search")
    public SearchResponse getMovie(@RequestBody SearchMovieRequest searchMovieDTO) {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setMovieSearch(movieService.getMovie(searchMovieDTO.getTextSearch()));
        searchResponse.setCastSearch(personsService.getCast(searchMovieDTO.getTextSearch()));
        return searchResponse;
    }

    // lay phim theo slug
    @GetMapping("/detail/{slug}")
    public ResponseEntity<MovieResponse> getMovieDetail(@PathVariable String slug) {
        MovieResponse response = movieService.getMovieDetail(slug);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<String> addMovie(@RequestBody MovieResponse movieResponse){
        movieService.addMovie(movieResponse);
        return ResponseEntity.ok("Thêm phim thành công!");
    }

    @DeleteMapping("/{id}")
    public void deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
    }

    @PostMapping("/{id}")
    public ResponseEntity<String> updateMovie(@PathVariable int id, @RequestBody MovieResponse movieResponse) {
        movieService.addMovie(movieResponse);
        return ResponseEntity.ok("Thêm phim thành công!");
    }

    @GetMapping("/genre")
    public List<Genres> getGenre() {
        return genresService.getAllGenres();
    }

    @GetMapping("/{id}/actor")
    public PersonResponse getActor(@PathVariable Long id) {
        return movieService.getPersons(id);
    }

    @GetMapping("/actor/{id}")
    public void getMovieOfActor(@PathVariable Long id) {
        personService.getMoviesOfCast(id);
    }

    @DeleteMapping("/actor/{id}")
    public void deleteActor(@PathVariable Long id) {
        personService.deleteCast(id);
    }

    @PostMapping("/actor")
    public void addActor(@RequestBody PersonDTO personDTO) {
  // them dien vien
    }

    @PatchMapping("/actor/{id}")
    public void updateActor(@PathVariable int id, @RequestBody PersonDTO personDTO) {
        // cap nhat dien vien
    }

    @PostMapping("/view/{id}")
    public ResponseEntity<String> increaseView(@PathVariable Long id) {
        movieService.increaseViewCount(id);
        return ResponseEntity.ok("View increased");
    }

    @GetMapping("/trending")
    public ResponseEntity<List<MovieResponse>> getTrending() {
        return ResponseEntity.ok(movieService.getTrendingMovies());
    }

    @GetMapping("/recommend")
    public ResponseEntity<List<MovieResponse>> getRecommendations() {
        return ResponseEntity.ok(recommendService.getRecommendedMovies());
    }
}
