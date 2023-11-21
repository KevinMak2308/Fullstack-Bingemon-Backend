package delta.fullstackbingemonbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class MovieService {
    @Value("${APIKEY}")
    private String apikey;

    private final String baseUrl = "https://api.themoviedb.org/3/";
    private final String movieUrl = "movie/";
    private final String collectionUrl = "collection/";
    private final String discoverUrl = "discover/movie";
    private final String searchUrl = "search/movie";
    private final String searchCollectionUrl = "search/collection";
    private final String genresUrl = "genre/movie/list";
    private final String apikeyUrl = "?api_key=";
    private final String genreUrl = "&with_genres=";

    private final String decadeUrl = "&primary_release_year=";

    private final String languageUrl = "&language=";

    private final String originalLanguageUrl = "&with_original_language=";

    private final String castUrl = "&with_cast=";

    private final String crewUrl = "&with_crew=";

    private final String watchRegionUrl = "&watch_region=";

    private final String watchProviderUrl = "&with_watch_providers=";

    private final String sortByUrl = "&sort_by=";

    private final String pageUrl = "&page=";

    private final String queryUrl = "&query=";
    private final String movieVideosUrl = "/videos";
    private final String movieCastUrl = "/credits";
    private final String backdropUrl = "&append_to_response=images";
    private final String imageUrl = "https://image.tmdb.org/t/p/original";
    private final String smallImageUrl = "https://image.tmdb.org/t/p/w300";
    private final String youtubeVideoUrl = "https://www.youtube.com/watch?v=";
    private final String recommendedMoviesUrl = "/recommendations";
    private final String movieWatchProvidersUrl = "/watch/providers";

    // movie url sammensætning: baseurl -> movieurl -> id -> apikeyurl -> apikey
    // discover url sammensætning: baseurl -> discoverurl -> apikeyurl -> apikey

    @SneakyThrows
    public JsonNode getMovie(Integer id) {
        URL url = new URL(baseUrl + movieUrl + id + apikeyUrl + apikey);
        return objectMapper(url);
    }

    @SneakyThrows
    public JsonNode getMovieCast(Integer id) {
        URL url = new URL(baseUrl + movieUrl + id + movieCastUrl + apikeyUrl + apikey);
        JsonNode cast = objectMapper(url).get("cast");
        if (cast != null && cast.isArray()) {
            for (JsonNode actor : cast) {
                ((ObjectNode) actor).put("profile_path", smallImageUrl + actor.get("profile_path").asText());
            }
        }
        return cast;
    }

    @SneakyThrows
    public JsonNode getMovieDirectors(Integer id) {
        URL url = new URL(baseUrl + movieUrl + id + movieCastUrl + apikeyUrl + apikey);
        JsonNode directors = objectMapper(url).get("crew");
        if (directors != null && directors.isArray()) {
            Iterator<JsonNode> iterator = directors.iterator();
            while (iterator.hasNext()) {
                JsonNode director = iterator.next();
                if (!director.has("job") || !director.get("job").asText().equalsIgnoreCase("Director")) {
                    iterator.remove();
                } else {
                    ((ObjectNode) director).put("profile_path", smallImageUrl + director.get("profile_path").asText());
                }
            }
        }
        return directors;
    }

    @SneakyThrows
    public JsonNode getMovieTrailer(Integer id) {
        URL url = new URL(baseUrl + movieUrl + id + movieVideosUrl + apikeyUrl + apikey);
        JsonNode results = objectMapper(url).get("results");
        List<JsonNode> videoList = new ArrayList<>();
        if (results != null && results.isArray()) {
            for (JsonNode trailer : results) {
                if (trailer.get("type").asText().trim().equalsIgnoreCase("Trailer") && trailer.get("official").asText().equalsIgnoreCase("true")) {
                    String key = trailer.get("key").asText();
                    ((ObjectNode) trailer).put("key", youtubeVideoUrl + key);
                    videoList.add(trailer);
                }
            }
        }
        return videoList.get(0);
    }
    @SneakyThrows
    public List<JsonNode> getMovieBackdrops(Integer id) {
        URL url = new URL(baseUrl + movieUrl + id + apikeyUrl + apikey + backdropUrl);
        JsonNode backdrops = objectMapper(url).get("images").get("backdrops");
        List<JsonNode> backdropList = new ArrayList<>();
        if (backdrops != null && backdrops.isArray()) {
            for (JsonNode backdrop : backdrops) {
                if (backdrop.get("iso_639_1").asText().trim().equalsIgnoreCase("null") && backdrop.get("aspect_ratio").asText().equalsIgnoreCase("1.778") && backdrop.get("height").asInt() >= 1080) {
                    String filePath = backdrop.get("file_path").asText();
                    if (filePath.startsWith("/")) {
                        ((ObjectNode) backdrop).put("file_path", imageUrl + filePath);
                    }
                    backdropList.add(backdrop);
                }
            }
        }
        return backdropList;
    }

    @SneakyThrows
    public JsonNode getMovieWatchProviders(Integer id, String watchRegion) {
        URL url = new URL(baseUrl + movieUrl + id + movieWatchProvidersUrl + apikeyUrl + apikey);
        return objectMapper(url).get("results").get(watchRegion);
    }

    @SneakyThrows
    public List<JsonNode> getRecommendedMovies(Integer id) {
        URL url = new URL(baseUrl + movieUrl + id + recommendedMoviesUrl + apikeyUrl + apikey);
        JsonNode movies = objectMapper(url).get("results");
        List<JsonNode> movieList = new ArrayList<>();
        if (movies != null && movies.isArray()) {
            for (JsonNode movie : movies) {
                movieList.add(movie);
            }
        }
        Collections.sort(movieList, (movie1, movie2) -> {
            double popularity1 = movie1.get("popularity").asDouble();
            double popularity2 = movie2.get("popularity").asDouble();
            return Double.compare(popularity2, popularity1);
        });
        return movieList;
    }

    @SneakyThrows
    public JsonNode getMovieCollection(Integer id) {
        URL url = new URL(baseUrl + collectionUrl + id + apikeyUrl + apikey);
        return objectMapper(url);
    }

    @SneakyThrows
    public JsonNode getAllGenres() {
        URL url = new URL(baseUrl + genresUrl + apikeyUrl + apikey);
        return objectMapper(url);
    }

    @SneakyThrows
    public List<JsonNode> searchMovies(String query, Integer year, Integer page) {
        if (query != "") query = query.replaceAll(" ", "%20");
        StringBuilder builder = new StringBuilder(baseUrl + searchUrl + apikeyUrl + apikey);
        addQueryParam(builder, queryUrl, query);
        if (year != null) addQueryParam(builder, decadeUrl, String.valueOf(year));
        if (page != null) addQueryParam(builder, pageUrl, String.valueOf(page));
        URL url = new URL(builder.toString());
        JsonNode results = objectMapper(url).get("results");
        List<JsonNode> movieList = new ArrayList<>();
        if (results != null && results.isArray()) {
            for (JsonNode movie : results) {
                movieList.add(movie);
            }
        }
        Collections.sort(movieList, (movie1, movie2) -> {
            double popularity1 = movie1.get("popularity").asDouble();
            double popularity2 = movie2.get("popularity").asDouble();
            return Double.compare(popularity2, popularity1);
        });
        return movieList;
    }

    @SneakyThrows
    public List<JsonNode> searchMovieCollections(Integer resultsPerPage, String query, Integer page) {
        if (resultsPerPage == null) resultsPerPage = 20;
        if (query != "") query = query.replaceAll(" ", "%20");
        if (page == null || page == 0) page = 1;
        StringBuilder builder = new StringBuilder(baseUrl + searchCollectionUrl + apikeyUrl + apikey);
        addQueryParam(builder, queryUrl, query);
        List<JsonNode> collectionsCustomLimit = new ArrayList<>();
        while (collectionsCustomLimit.size() < resultsPerPage) {
            addQueryParam(builder, pageUrl, String.valueOf(page));
            URL url = new URL(builder.toString());
            JsonNode collections = objectMapper(url).get("results");
            if (collections.isEmpty()) {
                break;
            }
            for (JsonNode collection : collections) {
                if (collectionsCustomLimit.size() == resultsPerPage || collection == null) break;
                collectionsCustomLimit.add(collection);
            }
            page++;
        }
        Collections.sort(collectionsCustomLimit, (collection1, collection2) -> {
            int id1 = collection1.get("id").asInt();
            int id2 = collection2.get("id").asInt();
            return Integer.compare(id1, id2);
        });
        return collectionsCustomLimit;
    }

    @SneakyThrows
    public List<JsonNode> discoverMovies(Integer resultsPerPage, String genres, String decade, String language, String originalLanguage, String cast, String crew, String watchRegion, String watchProviders, String sortBy, Integer page) {
        if (resultsPerPage == null) resultsPerPage = 20;
        if (page == null || page == 0) page = 1;
        StringBuilder builder = new StringBuilder(baseUrl + discoverUrl + apikeyUrl + apikey);
        addQueryParam(builder, genreUrl, genres);
        addQueryParam(builder, decadeUrl, decade);
        addQueryParam(builder, languageUrl, language);
        addQueryParam(builder, originalLanguageUrl, originalLanguage);
        addQueryParam(builder, castUrl, cast);
        addQueryParam(builder, crewUrl, crew);
        addQueryParam(builder, watchRegionUrl, watchRegion);
        addQueryParam(builder, watchProviderUrl, watchProviders);
        addQueryParam(builder, sortByUrl, sortBy);
        List<JsonNode> moviesCustomLimit = new ArrayList<>();
        while (moviesCustomLimit.size() < resultsPerPage) {
            addQueryParam(builder, pageUrl, String.valueOf(page));
            URL url = new URL(builder.toString());
            JsonNode movies = objectMapper(url).get("results");
            if (movies.isEmpty()) {
                break;
            }
            for (JsonNode movie : movies) {
                if (moviesCustomLimit.size() == resultsPerPage) break;
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode movieJsonNode = objectMapper.createObjectNode();
                movieJsonNode.put("id", movie.get("id").asInt());
                movieJsonNode.put("title", movie.get("title").asText());
                movieJsonNode.put("poster_path", smallImageUrl + movie.get("poster_path").asText());
                moviesCustomLimit.add(movieJsonNode);
            }
            page++;
        }
        return moviesCustomLimit;
    }

    private void addQueryParam(StringBuilder builder, String name, String value) {
        if (value != null && value.length() != 0) {
            builder.append(name).append(value);
        }
    }

    @SneakyThrows
    public JsonNode objectMapper(URL url) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(url);
    }
}
