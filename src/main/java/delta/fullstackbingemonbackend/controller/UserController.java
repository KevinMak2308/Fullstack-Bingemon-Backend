package delta.fullstackbingemonbackend.controller;

import com.fasterxml.jackson.databind.JsonNode;
import delta.fullstackbingemonbackend.model.Movie;
import delta.fullstackbingemonbackend.model.Series;
import delta.fullstackbingemonbackend.model.User;
import delta.fullstackbingemonbackend.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@CrossOrigin
@RequestMapping("api/user")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> user(@PathVariable Long id) {
        User user = userService.findById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @SneakyThrows
    @GetMapping("/avatars")
    public ResponseEntity<?> avatars() {
        List<JsonNode> avatars = userService.getAllAvatars();
        return new ResponseEntity<>(avatars, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> update(@RequestBody User user, @PathVariable Long id) {
        User updatedUser = userService.updateUser(user, id);
        return ResponseEntity.ok().body(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/movies")
    public ResponseEntity<List<Movie>> userMovieList(@PathVariable Long id) {
        List<Movie> movies = userService.findMovieList(id);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @PostMapping("/{id}/movie")
    public ResponseEntity<Long> addMovieToDB(@PathVariable Long id, @RequestBody Movie movie){
        userService.addMovieToDB(id, movie);
        return new ResponseEntity<>(movie.getId(), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/series")
    public ResponseEntity<List<Series>> userSeriesList(@PathVariable Long id) {
        List<Series> series = userService.findSeriesList(id);
        return new ResponseEntity<>(series, HttpStatus.OK);
    }

    @PostMapping("/{id}/series")
    public ResponseEntity<Long> addSeriesToDB(@PathVariable Long id, @RequestBody Series series){
        userService.addSeriesToDB(id, series);
        return new ResponseEntity<>(series.getId(), HttpStatus.CREATED);
    }

    @GetMapping("/compare-movies")
    public ResponseEntity<List<Movie>> compareMovies(@RequestParam Long loggedInId, @RequestParam Long friendId) {
        List<Movie> commonMovieList = userService.getMoviesInCommon(loggedInId, friendId);
        return new ResponseEntity<>(commonMovieList, HttpStatus.OK);
    }

    @GetMapping("/compare-series")
    public ResponseEntity<List<Series>> compareSeries(@RequestParam Long loggedInId, @RequestParam Long friendId) {
        List<Series> commonSeriesList = userService.getSeriesInCommon(loggedInId, friendId);
        return new ResponseEntity<>(commonSeriesList, HttpStatus.OK);
    }

    @GetMapping("/{id}/friends")
    public ResponseEntity<Set<Long>> getFriendList(@PathVariable Long id) {
        Set<Long> friends = userService.getFriendList(id);
        return new ResponseEntity<>(friends, HttpStatus.OK);
    }
}
