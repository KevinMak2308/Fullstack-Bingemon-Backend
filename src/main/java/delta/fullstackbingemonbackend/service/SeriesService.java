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
    private final String seriesTrailerUrl = "&append_to_response=videos";
    private final String seriesCastUrl = "/aggregate_credits";
    private final String backdropUrl = "&append_to_response=images";
    private final String imageUrl = "https://image.tmdb.org/t/p/original";
    private final String smallImageUrl = "https://image.tmdb.org/t/p/w300";
    private final String youtubeVideoUrl = "https://www.youtube.com/watch?v=";


    // Series url sammensætning: baseurl -> seriesurl -> id -> apikeyurl -> apikey
    // discover url sammensætning: baseurl -> discoverurl -> apikeyurl -> apikey

    @SneakyThrows
    public JsonNode getSeries(Integer id) {
        URL url = new URL(baseUrl + seriesUrl + id + apikeyUrl + apikey);
        System.out.println("URL for getSeries: " + url);
        return objectMapper(url);
    }

    @SneakyThrows
    public List<JsonNode> getSeriesCast(Integer id) {
        URL url = new URL(baseUrl + seriesUrl + id + seriesCastUrl + apikeyUrl + apikey);
        JsonNode cast = objectMapper(url).get("cast");
        List<JsonNode> castList = new ArrayList<>();
        if (cast != null && cast.isArray()) {
            int actorCount = 0;
            for (JsonNode actor : cast) {
                if (actor.get("known_for_department").asText().equalsIgnoreCase("Acting")) {
                    ((ObjectNode) actor).put("profile_path", smallImageUrl + actor.get("profile_path").asText());
                    castList.add(actor);
                    actorCount++;
                    if (actorCount == 24) {
                        break;
                    }
                }
            }
        }
        return castList;
    }

    @SneakyThrows
    public List<JsonNode> getSeriesCreators(Integer id) {
        URL url = new URL(baseUrl + seriesUrl + id + apikeyUrl + apikey);
        JsonNode creators = objectMapper(url).get("created_by");
        List<JsonNode> creatorList = new ArrayList<>();
        if (creators != null && creators.isArray()) {
            for (JsonNode creator : creators) {
                creatorList.add(creator);
            }
        }
        System.out.println("HELLO THIS IS A TEST");
        return creatorList;
    }

    @SneakyThrows
    public JsonNode getSeriesTrailer(Integer id) {
        URL url = new URL(baseUrl + seriesUrl + id + apikeyUrl + apikey + seriesTrailerUrl);
        JsonNode movie = objectMapper(url);
        JsonNode results = movie.get("videos").get("results");
        List<JsonNode> videoList = new ArrayList<>();
        if (results != null && results.isArray()) {
            for (JsonNode trailer : results) {
                if (trailer.get("type").asText().trim().equalsIgnoreCase("Trailer") && trailer.get("official").asText().equalsIgnoreCase("true")){
                    String key = trailer.get("key").asText();
                    ((ObjectNode) trailer).put("key", youtubeVideoUrl + key);
                    videoList.add(trailer);
                }
            }
        }
        return videoList.isEmpty() ? null : videoList.get(0);
    }

    @SneakyThrows
    public List<JsonNode> getSeriesBackdrops(Integer id) {
        URL url = new URL(baseUrl + seriesUrl + id + apikeyUrl + apikey + backdropUrl);
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
    public List<JsonNode> getSeriesSeasons(Integer id) {
        URL url = new URL(baseUrl + seriesUrl + id + apikeyUrl + apikey + backdropUrl);
        JsonNode seasons = objectMapper(url).get("seasons");
        List<JsonNode> seasonList = new ArrayList<>();
        if (seasons != null && seasons.isArray()) {
            for (JsonNode season : seasons) {
                String posterPath = season.get("poster_path").asText();
                if (season.get("season_number").asInt() > 0) {
                    ((ObjectNode) season).put("poster_path", imageUrl + posterPath);
                    seasonList.add(season);
                }
            }
        }
        return seasonList;
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
                System.out.println("JUMPSCARE PENIS");
                break;
            }
            for (JsonNode singleSeries : series) {
                if (seriesCustomLimit.size() == resultsPerPage) break;
                ObjectMapper objectMapper = new ObjectMapper();
                ObjectNode seriesJsonNode = objectMapper.createObjectNode();
                seriesJsonNode.put("id", singleSeries.get("id").asInt());
                seriesJsonNode.put("name", singleSeries.get("name").asText());
                seriesJsonNode.put("poster_path", smallImageUrl + singleSeries.get("poster_path").asText());
                seriesCustomLimit.add(seriesJsonNode);
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
