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


    @SneakyThrows
    public JsonNode getPerson(Integer id) {
        URL url = new URL(baseUrl + personUrl + id + apikeyUrl + apikey);
        JsonNode person = objectMapper(url);
        return person;
    }

    @SneakyThrows
    public List<JsonNode> getCombinedCredits(Integer id) {
        URL url = new URL(baseUrl + personUrl + id + combinedCreditsUrl + apikeyUrl + apikey);
        JsonNode credits = objectMapper(url);
        JsonNode movies = credits.get("cast");
        List<JsonNode> creditList = new ArrayList<>();
        if (movies != null && movies.isArray()) {
            for (JsonNode movie : movies) {
                creditList.add(movie);
            }
        }
        Collections.sort(creditList, (media1, media2) -> {
            int episodeCount1 = media1.get("popularity").asInt();
            int episodeCount2 = media2.get("popularity").asInt();
            return Integer.compare(episodeCount2, episodeCount1);
        });
        return creditList;
    }

    @SneakyThrows
    public List<JsonNode> getMovieCredits(Integer id) {
        URL url = new URL(baseUrl + personUrl + id + movieCreditsUrl + apikeyUrl + apikey);
        JsonNode credits = objectMapper(url);
        JsonNode movies = credits.get("cast");
        List<JsonNode> creditList = new ArrayList<>();
        if (movies != null && movies.isArray()) {
            for (JsonNode movie : movies) {
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
        JsonNode credits = objectMapper(url);
        JsonNode series = credits.get("cast");
        List<JsonNode> creditList = new ArrayList<>();
        if (series != null && series.isArray()) {
            for (JsonNode tvSeries : series) {
                creditList.add(tvSeries);
            }
        }
        Collections.sort(creditList, (series1, series2) -> {
            int popularity1 = series1.get("episode_count").asInt();
            int popularity2 = series2.get("episode_count").asInt();
            return Integer.compare(popularity2, popularity1);
        });
        return creditList;
    }

    @SneakyThrows
    public List<JsonNode> searchPeople(String query, Integer page) {
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
    public List<JsonNode> getPopularPeople(Integer page) {
        StringBuilder builder = new StringBuilder(baseUrl + popularPeopleUrl + apikeyUrl + apikey);
        if (page != null) addQueryParam(builder, pageUrl, String.valueOf(page));
        URL url = new URL(builder.toString());
        JsonNode people = objectMapper(url);
        JsonNode results = people.get("results");
        List<JsonNode> peopleList = new ArrayList<>();
        if (results != null && results.isArray()) {
            for (JsonNode person : results) {
                ObjectMapper mapper = new ObjectMapper();
                ObjectNode newPerson = mapper.createObjectNode();
                newPerson.put("name", person.get("name"));
                newPerson.put("profile_path", person.get("profile_path"));
                newPerson.put("popularity", person.get("popularity"));
                newPerson.put("known_for_department", person.get("known_for_department"));
                person = mapper.treeToValue(newPerson, JsonNode.class);
                peopleList.add(person);
            }
        }
        Collections.sort(peopleList, (person1, person2) -> {
            double popularity1 = person1.get("popularity").asDouble();
            double popularity2 = person2.get("popularity").asDouble();
            return Double.compare(popularity2, popularity1);
        });

        return peopleList;
    }

    @SneakyThrows
    public List<JsonNode> getDirectors() {
        List<JsonNode> directors = new ArrayList<>();
        Integer page = 1;
        while (directors.size() < 20) {
            List<JsonNode> people = getPopularPeople(page);
            for (JsonNode person : people) {
                if (person.get("known_for_department").asText().equalsIgnoreCase("directing")) {
                    directors.add(person);
                    System.out.println(person.get("name") + ": " + person.get("known_for_department"));
                }
            }
            page++;
        }
        System.out.println(directors);
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
