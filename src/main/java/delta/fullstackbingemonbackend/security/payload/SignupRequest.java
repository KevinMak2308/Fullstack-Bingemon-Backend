package delta.fullstackbingemonbackend.security.payload;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
public class SignupRequest {

    @NonNull
    @Size(max = 40)
    private String username;

    @NonNull
    @Size(max = 255)
    private String name;

    @NonNull
    @Size(max = 255)
    private String password;

    @NonNull
    @Size(max = 255)
    private String email;
}
