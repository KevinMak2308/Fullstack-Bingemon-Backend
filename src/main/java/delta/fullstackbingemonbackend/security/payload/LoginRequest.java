package delta.fullstackbingemonbackend.security.payload;

import lombok.*;

@Getter
@Setter
public class LoginRequest {

    @NonNull
    private String username;

    @NonNull
    private String password;
}
