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
import java.util.Objects;

@Service
public class PersonService {
    @Value("${APIKEY}")
    private String apikey;
    private final String baseUrl = "https://api.themoviedb.org/3/";
    private final String personUrl = "person/";
    private final String combinedCreditsUrl = "/combined_credits";
    private final String movieCreditsUrl = "/movie_credits";
    private final String seriesCreditsUrl = "/tv_credits";
    private final String searchUrl = "search/person";
    private final String popularPeopleUrl = "person/popular";
    private final String pageUrl = "&page=";
    private final String queryUrl = "&query=";
    private final String apikeyUrl = "?api_key=";
    private final String smallImageUrl = "https://image.tmdb.org/t/p/w200";


    @SneakyThrows
    public JsonNode getPerson(Integer id) {
        URL url = new URL(baseUrl + personUrl + id + apikeyUrl + apikey);
        return objectMapper(url);
    }

    @SneakyThrows
    public List<JsonNode> getCombinedCredits(Integer id) {
        URL url = new URL(baseUrl + personUrl + id + combinedCreditsUrl + apikeyUrl + apikey);
        JsonNode credits = objectMapper(url).get("cast");
        List<JsonNode> creditList = new ArrayList<>();
        if (credits != null && credits.isArray()) {
            for (JsonNode credit : credits) {
                creditList.add(credit);
            }
        }
        Collections.sort(creditList, (media1, media2) -> {
            double popularity1 = media1.get("popularity").asDouble();
            double popularity2 = media2.get("popularity").asDouble();
            return Double.compare(popularity2, popularity1);
        });
        return creditList;
    }

    @SneakyThrows
    public List<JsonNode> getMovieCredits(Integer id) {
        URL url = new URL(baseUrl + personUrl + id + movieCreditsUrl + apikeyUrl + apikey);
        JsonNode moviesCredits = objectMapper(url).get("cast");
        List<JsonNode> creditList = new ArrayList<>();
        if (moviesCredits != null && moviesCredits.isArray()) {
            for (JsonNode movie : moviesCredits) {
                creditList.add(movie);
            }
        }
        Collections.sort(creditList, (movie1, movie2) -> {
            double popularity1 = movie1.get("popularity").asDouble();
            double popularity2 = movie2.get("popularity").asDouble();
            return Double.compare(popularity2, popularity1);
        });
        return creditList;
    }

    @SneakyThrows
    public List<JsonNode> getSeriesCredits(Integer id) {
        URL url = new URL(baseUrl + personUrl + id + seriesCreditsUrl + apikeyUrl + apikey);
        JsonNode seriesCredits = objectMapper(url).get("cast");
        List<JsonNode> creditList = new ArrayList<>();
        if (seriesCredits != null && seriesCredits.isArray()) {
            for (JsonNode tvSeries : seriesCredits) {
                creditList.add(tvSeries);
            }
        }
        Collections.sort(creditList, (series1, series2) -> {
            double popularity1 = series1.get("popularity").asDouble();
            double popularity2 = series2.get("popularity").asDouble();
            return Double.compare(popularity2, popularity1);
        });
        return creditList;
    }

    @SneakyThrows
    public List<JsonNode> searchPeople(String query, Integer page) {
        if (query != "") query = query.replaceAll(" ", "%20");
        StringBuilder builder = new StringBuilder(baseUrl + searchUrl + apikeyUrl + apikey);
        addQueryParam(builder, queryUrl, query);
        if (page != null) addQueryParam(builder, pageUrl, String.valueOf(page));
        URL url = new URL(builder.toString());
        JsonNode people = objectMapper(url).get("results");
        List<JsonNode> peopleList = new ArrayList<>();
        if (people != null && people.isArray()) {
            for (JsonNode person : people) {
                peopleList.add(person);
            }
        }
        Collections.sort(peopleList, (series1, series2) -> {
            double popularity1 = series1.get("popularity").asDouble();
            double popularity2 = series2.get("popularity").asDouble();
            return Double.compare(popularity2, popularity1);
        });
        return peopleList;
    }

    @SneakyThrows
    public List<JsonNode> getPopularPeople(Integer resultsPerPage, Integer page) {
        if (resultsPerPage == null) resultsPerPage = 20;
        if (page == null || page == 0) page = 1;
        StringBuilder builder = new StringBuilder(baseUrl + popularPeopleUrl + apikeyUrl + apikey);
        List<JsonNode> peopleList = new ArrayList<>();
        while (peopleList.size() < resultsPerPage) {
            if (page != null) addQueryParam(builder, pageUrl, String.valueOf(page));
            URL url = new URL(builder.toString());
            JsonNode people = objectMapper(url).get("results");
            if (people.isEmpty()) {
                break;
            }
            for (JsonNode person : people) {
                if (peopleList.size() == resultsPerPage) break;
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode newPerson = mapper.createObjectNode();
                newPerson.put("id", person.get("id"));
                newPerson.put("name", person.get("name"));
                JsonNode profilePath = person.get("profile_path");
                if (profilePath != null && !profilePath.asText().isEmpty()) {
                    ((ObjectNode) person).put("profile_path", smallImageUrl + profilePath);
                    newPerson.put("profile_path", person.get("profile_path"));
                }
                newPerson.put("popularity", person.get("popularity"));
                newPerson.put("known_for_department", person.get("known_for_department"));
                person = mapper.treeToValue(newPerson, JsonNode.class);
                peopleList.add(person);
            }
            page++;
        }
        Collections.sort(peopleList, (person1, person2) -> {
            double popularity1 = person1.get("popularity").asDouble();
            double popularity2 = person2.get("popularity").asDouble();
            return Double.compare(popularity2, popularity1);
        });
        return peopleList;
    }

    @SneakyThrows
    public List<JsonNode> getActors(Integer resultsPerPage) {
        if (resultsPerPage == null) resultsPerPage = 20;
        List<JsonNode> actors = new ArrayList<>();
        Integer page = 1;
        while (actors.size() < resultsPerPage) {
            List<JsonNode> people = getPopularPeople(null, page);
            for (JsonNode person : people) {
                if (person.get("known_for_department").asText().equalsIgnoreCase("acting")) {
                    actors.add(person);
                }
            }
            page++;
        }
        return actors;
    }

    @SneakyThrows
    public List<JsonNode> getDirectors(Integer resultsPerPage) {
        if (resultsPerPage == null) resultsPerPage = 5;
        List<JsonNode> directors = new ArrayList<>();
        Integer page = 1;
        while (directors.size() < resultsPerPage) {
            List<JsonNode> people = getPopularPeople(null, page);
            for (JsonNode person : people) {
                if (person.get("known_for_department").asText().equalsIgnoreCase("directing")) {
                    directors.add(person);
                }
            }
            page++;
        }
        return directors;
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
