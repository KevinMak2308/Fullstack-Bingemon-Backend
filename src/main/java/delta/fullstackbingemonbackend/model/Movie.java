package delta.fullstackbingemonbackend.model;

import javax.persistence.*;

import lombok.*;
import java.util.Objects;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @Column(name = "id")
    private Long id;

    @NonNull
    @Column(name = "title")
    private String title;

    @Column(name = "poster_path")
    private String poster_path;

    @Column(name = "popularity")
    private Double popularity;

    @Column(name = "vote_average")
    private Double vote_average;

    @Column(name = "release_date")
    private String release_date;

    public Movie(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(id, movie.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
