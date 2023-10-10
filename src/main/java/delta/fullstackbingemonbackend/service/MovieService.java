package delta.fullstackbingemonbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MovieService {
    @Value("${APIKEY}")
    private String apikey;

    private final String baseUrl = "https://api.themoviedb.org/3/";
    private final String movieUrl = "movie/";
    private final String discoverUrl = "discover/movie";
    private final String apikeyUrl = "?api_key=";

    private String genreUrl = "&with_genres=";

    private String decadeUrl = "&primary_release_year=";

    private String languageUrl = "&language=";

    private String originalLanguageUrl = "&with_original_language=";

    private String castUrl = "&with_cast=";

    private String crewUrl = "&with_crew=";

    private String watchRegionUrl = "&watch_region=";

    private String watchProviderUrl = "&with_watch_providers=";

    private String sortByUrl = "&sort_by=";

    private String pageUrl = "&page=";

    // movie url sammensætning: baseurl -> movieurl -> id -> apikeyurl -> apikey
    // discover url sammensætning: baseurl -> discoverurl -> apikeyurl -> apikey

    public JsonNode getMovie(Integer id) {
        RestTemplate restTemplate = new RestTemplate();
        String url = baseUrl + movieUrl + id + apikeyUrl + apikey;
        return restTemplate.getForObject(url, JsonNode.class);
    }

    public JsonNode discoverMovies(String genres, String decade, String language, String originalLanguage, String cast, String crew, String watchRegion, String watchProviders, String sortBy, Integer page) {
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
        if (page != null) addQueryParam(builder, pageUrl, String.valueOf(page));
        RestTemplate restTemplate = new RestTemplate();
        String url = builder.toString();
        return restTemplate.getForObject(url, JsonNode.class);
    }

    private void addQueryParam(StringBuilder builder, String name, String value) {
        if (value != null && value.length() != 0) {
            builder.append(name).append(value);
        }
    }
}
