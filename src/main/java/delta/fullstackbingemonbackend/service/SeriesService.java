package delta.fullstackbingemonbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class SeriesService {
    @Value("${APIKEY}")
    private String apikey;

    private final String baseUrl = "https://api.themoviedb.org/3/";
    private final String seriesUrl = "tv/";
    private final String seasonUrl = "/season/";
    private final String discoverUrl = "discover/tv";
    private final String searchUrl = "search/tv";
    private final String getGenresUrl = "genre/tv/list";
    private final String apikeyUrl = "?api_key=";
    private final String genreUrl = "&with_genres=";
    private final String decadeUrl = "&first_air_date_year=";

    private final String languageUrl = "&language=";

    private final String originalLanguageUrl = "&with_original_language=";

    private final String watchRegionUrl = "&watch_region=";

    private final String watchProviderUrl = "&with_watch_providers=";

    private final String sortByUrl = "&sort_by=";

    private final String pageUrl = "&page=";

    private final String queryUrl = "&query=";
    private final String seriesDetailsUrl = "&append_to_response=credits,videos,images";

    // Series url sammensætning: baseurl -> seriesurl -> id -> apikeyurl -> apikey
    // discover url sammensætning: baseurl -> discoverurl -> apikeyurl -> apikey

    @SneakyThrows
    public JsonNode getSeries(Integer id) {
        URL url = new URL(baseUrl + seriesUrl + id + apikeyUrl + apikey + seriesDetailsUrl);
        return objectMapper(url);
    }

    @SneakyThrows
    public JsonNode getSeriesSeason(Integer seriesId, Integer seasonNumber) {
        URL url = new URL(baseUrl + seriesUrl + seriesId + seasonUrl + seasonNumber + apikeyUrl + apikey);
        return objectMapper(url);
    }

    @SneakyThrows
    public JsonNode getAllGenres() {
        URL url = new URL(baseUrl + getGenresUrl + apikeyUrl + apikey);
        return objectMapper(url);
    }

    @SneakyThrows
    public List<JsonNode> searchSeries(String query, Integer page) {
        if (query != "") query = query.replaceAll(" ", "%20");
        StringBuilder builder = new StringBuilder(baseUrl + searchUrl + apikeyUrl + apikey);
        addQueryParam(builder, queryUrl, query);
        if (page != null) addQueryParam(builder, pageUrl, String.valueOf(page));
        URL url = new URL(builder.toString());
        JsonNode results = objectMapper(url).get("results");
        List<JsonNode> seriesList = new ArrayList<>();
        if (results != null && results.isArray()) {
            for (JsonNode series : results) {
                seriesList.add(series);
            }
        }
        Collections.sort(seriesList, (series1, series2) -> {
            double popularity1 = series1.get("popularity").asDouble();
            double popularity2 = series2.get("popularity").asDouble();
            return Double.compare(popularity2, popularity1);
        });
        return seriesList;
    }

    @SneakyThrows
    public List<JsonNode> discoverSeries(Integer resultsPerPage, String genres, String decade, String language, String originalLanguage, String watchRegion, String watchProviders, String sortBy, Integer page) {
        if (resultsPerPage == null) resultsPerPage = 20;
        if (page == null || page == 0) page = 1;
        StringBuilder builder = new StringBuilder(baseUrl + discoverUrl + apikeyUrl + apikey);
        addQueryParam(builder, genreUrl, genres);
        addQueryParam(builder, decadeUrl, decade);
        addQueryParam(builder, languageUrl, language);
        addQueryParam(builder, originalLanguageUrl, originalLanguage);
        addQueryParam(builder, watchRegionUrl, watchRegion);
        addQueryParam(builder, watchProviderUrl, watchProviders);
        addQueryParam(builder, sortByUrl, sortBy);
        List<JsonNode> seriesCustomLimit = new ArrayList<>();
        while (seriesCustomLimit.size() < resultsPerPage) {
            addQueryParam(builder, pageUrl, String.valueOf(page));
            URL url = new URL(builder.toString());
            JsonNode series = objectMapper(url).get("results");
            if (series.isEmpty()) {
                break;
            }
            for (JsonNode singleSeries : series) {
                if (seriesCustomLimit.size() == resultsPerPage) break;
                seriesCustomLimit.add(singleSeries);
            }
            page++;
        }
        return seriesCustomLimit;
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
