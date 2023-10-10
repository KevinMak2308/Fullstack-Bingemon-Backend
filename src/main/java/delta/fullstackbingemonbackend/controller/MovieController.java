package delta.fullstackbingemonbackend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import delta.fullstackbingemonbackend.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    @GetMapping("/discover")
    public ResponseEntity<?> discoverMovies(@RequestParam(required = false) String genres, @RequestParam(required = false) String decade,
                                            @RequestParam(required = false) String language, @RequestParam(required = false) String original_language,
                                            @RequestParam(required = false) String cast, @RequestParam(required = false) String crew,
                                            @RequestParam(required = false) String watch_region, @RequestParam(required = false) String watch_providers,
                                            @RequestParam(required = false) String sort_by, @RequestParam(required = false) Integer page) {
        JsonNode movie = movieService.discoverMovies(genres, decade, language, original_language, cast, crew, watch_region, watch_providers, sort_by, page);
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }
}
