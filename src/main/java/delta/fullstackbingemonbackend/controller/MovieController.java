package delta.fullstackbingemonbackend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import delta.fullstackbingemonbackend.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/movie")
public class MovieController {

    private MovieService movieService;

    @Autowired
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMovie(@PathVariable Integer id) {
        JsonNode movie = movieService.getMovie(id);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @GetMapping("/{id}/cast")
    public ResponseEntity<?> getMovieCast(@PathVariable Integer id) {
        List<JsonNode> cast = movieService.getMovieCast(id);
        return new ResponseEntity<>(cast, HttpStatus.OK);
    }

    @GetMapping("/{id}/directors")
    public ResponseEntity<?> getMovieDirectors(@PathVariable Integer id) {
        List<JsonNode> directors = movieService.getMovieDirectors(id);
        return new ResponseEntity<>(directors, HttpStatus.OK);
    }

    @GetMapping("/{id}/trailer")
    public ResponseEntity<?> getMovieTrailer(@PathVariable Integer id) {
        JsonNode trailer = movieService.getMovieTrailer(id);
        return new ResponseEntity<>(trailer, HttpStatus.OK);
    }

    @GetMapping("/{id}/backdrops")
    public ResponseEntity<?> getMovieBackdrops(@PathVariable Integer id) {
        List<JsonNode> backdrops = movieService.getMovieBackdrops(id);
        return new ResponseEntity<>(backdrops, HttpStatus.OK);
    }

    @GetMapping("/{id}/providers/{region}")
    public ResponseEntity<?> getMovieWatchProviders(@PathVariable Integer id, @PathVariable String region) {
        JsonNode watchProviders = movieService.getMovieWatchProviders(id, region);
        return new ResponseEntity<>(watchProviders, HttpStatus.OK);
    }

    @GetMapping("/{id}/recommended")
    public ResponseEntity<?> getRecommendedMovies(@PathVariable Integer id) {
        List<JsonNode> movies = movieService.getRecommendedMovies(id);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("/collection/{collectionId}")
    public ResponseEntity<?> getMovieCollection(@PathVariable Integer collectionId) {
        JsonNode movie = movieService.getMovieCollection(collectionId);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @GetMapping("/genres")
    public ResponseEntity<?> getAllGenres() {
        JsonNode genres = movieService.getAllGenres();
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchMovies(@RequestParam(required = false) String query, @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer page) {
        List<JsonNode> movies = movieService.searchMovies(query, year, page);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping({"/collection/search", "/collection/search/{results_per_page}"})
    public ResponseEntity<?> searchMovieCollections(@PathVariable(required = false) Integer results_per_page, @RequestParam(required = false) String query, @RequestParam(required = false) Integer page) {
        List<JsonNode> collections = movieService.searchMovieCollections(results_per_page, query, page);
        return new ResponseEntity<>(collections, HttpStatus.OK);
    }

    @GetMapping({"/discover", "/discover/{results_per_page}"})
    public ResponseEntity<?> discoverMovies(@PathVariable(required = false) Integer results_per_page,
                                            @RequestParam(required = false) String genres, @RequestParam(required = false) String decade,
                                            @RequestParam(required = false) String language, @RequestParam(required = false) String original_language,
                                            @RequestParam(required = false) String cast, @RequestParam(required = false) String crew,
                                            @RequestParam(required = false) String watch_region, @RequestParam(required = false) String watch_providers,
                                            @RequestParam(required = false) String sort_by, @RequestParam(required = false) Integer page) {
        List<JsonNode> movies = movieService.discoverMovies(results_per_page, genres, decade, language, original_language, cast, crew, watch_region, watch_providers, sort_by, page);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }
}
