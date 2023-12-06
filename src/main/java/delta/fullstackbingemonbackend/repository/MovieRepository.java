package delta.fullstackbingemonbackend.repository;

import delta.fullstackbingemonbackend.model.Movie;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@Qualifier("Movie")
public interface MovieRepository extends JpaRepository<Movie, Long> {

    List<Movie> findMoviesByIdIn(Set<Long> movieIds);

}
