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

    @GetMapping("/{id}/season/{seasonId}")
    public ResponseEntity<?> getSeriesSeason(@PathVariable Integer id, @PathVariable Integer seasonNumber) {
        JsonNode Series = seriesService.getSeriesSeason(id, seasonNumber);
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

    @GetMapping("/discover")
    public ResponseEntity<?> discoverSeries(@RequestParam(required = false) String genres, @RequestParam(required = false) String decade,
                                            @RequestParam(required = false) String language, @RequestParam(required = false) String original_language,
                                            @RequestParam(required = false) String watch_region, @RequestParam(required = false) String watch_providers,
                                            @RequestParam(required = false) String sort_by, @RequestParam(required = false) Integer page) {
        JsonNode Series = seriesService.discoverSeries(genres, decade, language, original_language, watch_region, watch_providers, sort_by, page);
        return new ResponseEntity<>(Series, HttpStatus.OK);
    }

    @GetMapping("/discover/{results_per_page}")
    public ResponseEntity<?> discoverSeries(@PathVariable Integer results_per_page, @RequestParam(required = false) String genres, @RequestParam(required = false) String decade,
                                            @RequestParam(required = false) String language, @RequestParam(required = false) String original_language,
                                            @RequestParam(required = false) String watch_region, @RequestParam(required = false) String watch_providers,
                                            @RequestParam(required = false) String sort_by, @RequestParam(required = false) Integer page) {
        List<JsonNode> Series = seriesService.discoverCustomAmountOfSeries(results_per_page, genres, decade, language, original_language, watch_region, watch_providers, sort_by, page);
        return new ResponseEntity<>(Series, HttpStatus.OK);
    }
}
