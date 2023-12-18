package delta.fullstackbingemonbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import delta.fullstackbingemonbackend.model.Movie;
import delta.fullstackbingemonbackend.model.Series;
import delta.fullstackbingemonbackend.model.User;
import delta.fullstackbingemonbackend.repository.MovieRepository;
import delta.fullstackbingemonbackend.repository.SeriesRepository;
import delta.fullstackbingemonbackend.repository.UserRepository;
import delta.fullstackbingemonbackend.security.jwt.JsonWebToken;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import javax.persistence.NoResultException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private UserRepository userRepository;
    private MovieRepository movieRepository;
    private SeriesRepository seriesRepository;

    private PasswordEncoder passwordEncoder;

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    private final JsonWebToken jsonWebToken;



    @Autowired
    public UserService(UserRepository userRepository, MovieRepository movieRepository, SeriesRepository seriesRepository, PasswordEncoder passwordEncoder, ResourceLoader resourceLoader, ObjectMapper objectMapper, JsonWebToken jsonWebToken) {
        this.userRepository = userRepository;
        this.movieRepository = movieRepository;
        this.seriesRepository = seriesRepository;
        this.passwordEncoder = passwordEncoder;
        this.resourceLoader = resourceLoader;
        this.objectMapper = objectMapper;
        this.jsonWebToken = jsonWebToken;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(()
                -> new NoResultException("User with id: " + id + " was not found"));
    }

    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @SneakyThrows
    public List<JsonNode> getAllAvatars() {
        Resource resource = resourceLoader.getResource("classpath:/static/avatars/");
        Path avatarsPath = resource.getFile().toPath();
        return Files.list(avatarsPath)
                .map(Path::getFileName)
                .map(Path::toString)
                .map(this::convertToJsonNode)
                .collect(Collectors.toList());
    }

    public User updateUser(User updatedUser, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NoResultException("User with ID: " + id + " could not be found"));
        user.setBio(updatedUser.getBio());
        user.setProfile_picture_filename(updatedUser.getProfile_picture_filename());
        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<Movie> findMovieList(Long id){
        User user = userRepository.findById(id).orElseThrow(()
                -> new NoResultException("User with id: " + id + " does not exist"));
        List<Movie> movies = movieRepository.findMoviesByIdIn(user.getMovieList());
        return movies;
    }

    public void addMovieToDB(Long id, Movie movie) {
        User user = userRepository.findById(id).orElseThrow(()
                -> new NoResultException("User with username: " + id + " does not exist"));
        Set<Long> movieList = user.getMovieList();
        movieList.add(movie.getId());
        userRepository.save(user);
        movie.setPoster_path("https://image.tmdb.org/t/p/w300" + movie.getPoster_path());
        movieRepository.save(movie);
    }

    public List<Series> findSeriesList(Long id){
        User user = userRepository.findById(id).orElseThrow(()
                -> new NoResultException("User with id: " + id + " does not exist"));
        List<Series> series = seriesRepository.findSeriesByIdIn(user.getSeriesList());
        return series;
    }

    public void addSeriesToDB(Long id, Series series) {
        User user = userRepository.findById(id).orElseThrow(()
                -> new NoResultException("User with username: " + id + " does not exist"));
        Set<Long> seriesList = user.getSeriesList();
        seriesList.add(series.getId());
        userRepository.save(user);
        series.setPoster_path("https://image.tmdb.org/t/p/w300" + series.getPoster_path());
        seriesRepository.save(series);
    }

    public List<Movie> getMoviesInCommon(Long loggedInID, Long friendId) {
        Set<Long> commonMovieIds = findCommonMovies(getUserById(loggedInID), getUserById(friendId));
        return movieRepository.findMoviesByIdIn(commonMovieIds);
    }

    private Set<Long> findCommonMovies(User user1, User user2) {
        Set<Long> moviesUser1 = new HashSet<>(user1.getMovieList());
        Set<Long> moviesUser2 = new HashSet<>(user2.getMovieList());

        moviesUser1.retainAll(moviesUser2);

        return moviesUser1;
    }

    public List<Series> getSeriesInCommon(Long loggedInID, Long friendId) {
        Set<Long> commonSeriesIds = findCommonSeries(getUserById(loggedInID), getUserById(friendId));
        return seriesRepository.findSeriesByIdIn(commonSeriesIds);
    }

    private Set<Long> findCommonSeries(User user1, User user2) {
        Set<Long> seriesUser1 = new HashSet<>(user1.getSeriesList());
        Set<Long> seriesUser2 = new HashSet<>(user2.getSeriesList());

        seriesUser1.retainAll(seriesUser2);

        return seriesUser1;
    }

    public Set<Long> getFriendList(Long id){
        return getUserById(id).getFriendList();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoResultException("User with id: " + userId + " does not exist"));
    }

    private JsonNode convertToJsonNode(String filename) {
        return objectMapper.createObjectNode().put("filename", filename);
    }
}
