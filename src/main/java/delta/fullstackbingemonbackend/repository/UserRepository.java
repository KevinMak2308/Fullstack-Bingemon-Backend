package delta.fullstackbingemonbackend.repository;

import delta.fullstackbingemonbackend.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Qualifier("User")
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByUsername(String username);
}
