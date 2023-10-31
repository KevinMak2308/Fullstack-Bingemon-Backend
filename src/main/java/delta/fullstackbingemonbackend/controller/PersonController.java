package delta.fullstackbingemonbackend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import delta.fullstackbingemonbackend.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/person")
public class PersonController {

    private PersonService personService;

    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPerson(@PathVariable Integer id) {
        JsonNode person = personService.getPerson(id);
        return new ResponseEntity<>(person, HttpStatus.OK);
    }

    @GetMapping("/{id}/credits/combined")
    public ResponseEntity<?> getCombinedCredits(@PathVariable Integer id) {
        List<JsonNode> combinedCredits = personService.getCombinedCredits(id);
        return new ResponseEntity<>(combinedCredits, HttpStatus.OK);
    }

    @GetMapping("/{id}/credits/movies")
    public ResponseEntity<?> getMovieCredits(@PathVariable Integer id) {
        List<JsonNode> movieCredits = personService.getMovieCredits(id);
        return new ResponseEntity<>(movieCredits, HttpStatus.OK);
    }

    @GetMapping("/{id}/credits/series")
    public ResponseEntity<?> getSeriesCredits(@PathVariable Integer id) {
        List<JsonNode> seriesCredits = personService.getSeriesCredits(id);
        return new ResponseEntity<>(seriesCredits, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchPeople(@RequestParam(required = false) String query, @RequestParam(required = false) Integer page) {
        List<JsonNode> people = personService.searchPeople(query, page);
        return new ResponseEntity<>(people, HttpStatus.OK);
    }

    @GetMapping({"/popular", "/popular/{results_per_page}"})
    public ResponseEntity<?> getPopularPeople(@PathVariable(required = false) Integer results_per_page, @RequestParam(required = false) Integer page) {
        List<JsonNode> people = personService.getPopularPeople(results_per_page, page);
        return new ResponseEntity<>(people, HttpStatus.OK);
    }

    @GetMapping({"/directors", "/directors/{results_per_page}"})
    public ResponseEntity<?> getDirectors(@PathVariable(required = false) Integer results_per_page) {
        List<JsonNode> directors = personService.getDirectors(results_per_page);
        return new ResponseEntity<>(directors, HttpStatus.OK);
    }
}
