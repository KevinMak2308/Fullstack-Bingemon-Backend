package delta.fullstackbingemonbackend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import delta.fullstackbingemonbackend.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin
@RequestMapping("/series")
public class SeriesController {

    private SeriesService seriesService;

    @Autowired
    public SeriesController(SeriesService seriesService) {
        this.seriesService = seriesService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSeries(@PathVariable Integer id) {
        JsonNode Series = seriesService.getSeries(id);
        return new ResponseEntity<>(Series, HttpStatus.OK);
    }

    @GetMapping("/{id}/cast")
    public ResponseEntity<?> getMovieCast(@PathVariable Integer id) {
        List<JsonNode> cast = seriesService.getSeriesCast(id);
        return new ResponseEntity<>(cast, HttpStatus.OK);
    }

    @GetMapping("/{id}/trailer")
    public ResponseEntity<?> getSeriesTrailer(@PathVariable Integer id) {
        JsonNode trailer = seriesService.getSeriesTrailer(id);
        return new ResponseEntity<>(trailer, HttpStatus.OK);
    }

    @GetMapping("/{id}/backdrops")
    public ResponseEntity<?> getSeriesBackdrops(@PathVariable Integer id) {
        List<JsonNode> backdrops = seriesService.getSeriesBackdrops(id);
        return new ResponseEntity<>(backdrops, HttpStatus.OK);
    }

    @GetMapping("/{id}/season/{seasonNumber}")
    public ResponseEntity<?> getSeriesSeason(@PathVariable Integer id, @PathVariable Integer seasonNumber) {
        JsonNode Series = seriesService.getSeriesSeason(id, seasonNumber);
        return new ResponseEntity<>(Series, HttpStatus.OK);
    }

    @GetMapping("/{id}/seasons")
    public ResponseEntity<?> getSeriesSeasons(@PathVariable Integer id) {
        List<JsonNode> Series = seriesService.getSeriesSeasons(id);
        return new ResponseEntity<>(Series, HttpStatus.OK);
    }

    @GetMapping("/genres")
    public ResponseEntity<?> getAllGenres() {
        JsonNode genres = seriesService.getAllGenres();
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchSeries(@RequestParam(required = false) String query, @RequestParam(required = false) Integer page) {
        List<JsonNode> Series = seriesService.searchSeries(query, page);
        return new ResponseEntity<>(Series, HttpStatus.OK);
    }

    @GetMapping({"/discover", "/discover/{results_per_page}"})
    public ResponseEntity<?> discoverSeries(@PathVariable Integer results_per_page, @RequestParam(required = false) String genres, @RequestParam(required = false) String decade,
                                            @RequestParam(required = false) String language, @RequestParam(required = false) String original_language,
                                            @RequestParam(required = false) String watch_region, @RequestParam(required = false) String watch_providers,
                                            @RequestParam(required = false) String sort_by, @RequestParam(required = false) Integer page) {
        List<JsonNode> Series = seriesService.discoverSeries(results_per_page, genres, decade, language, original_language, watch_region, watch_providers, sort_by, page);
        return new ResponseEntity<>(Series, HttpStatus.OK);
    }
}
