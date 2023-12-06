package delta.fullstackbingemonbackend.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@RequiredArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {

    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NonNull
    @Column(length = 40, name = "username")
    private String username;

    @NonNull
    @Column(name = "name")
    private String name;

    @NonNull
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String password;

    @NonNull
    @Column(name = "email")
    private String email;

    @Column(name = "bio")
    private String bio;

    @Column(name = "profile_picture_filename")
    private String profile_picture_filename;

    @ElementCollection
    @CollectionTable(name = "user_movie_list", joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "movie_id"})
    )
    @Column(name = "movie_id", nullable = false)
    private Set<Long> movieList = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "user_series_list", joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "series_id"})
    )
    @Column(name = "series_id", nullable = false)
    private Set<Long> seriesList = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "friends", joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"})
    )
    @Column(name = "friend_id", nullable = false)
    private Set<Long> friendList = new HashSet<>();

    public User(){}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
