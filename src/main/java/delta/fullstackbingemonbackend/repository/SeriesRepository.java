package delta.fullstackbingemonbackend.repository;

import delta.fullstackbingemonbackend.model.Series;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
@Qualifier("Series")
public interface SeriesRepository extends JpaRepository<Series, Long> {

    List<Series> findSeriesByIdIn(Set<Long> seriesIds);

}
