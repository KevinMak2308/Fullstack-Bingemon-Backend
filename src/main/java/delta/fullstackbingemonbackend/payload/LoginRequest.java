package delta.fullstackbingemonbackend.payload;

import lombok.*;

@Getter
@Setter
public class LoginRequest {

    @NonNull
    private String username;

    @NonNull
    private String password;
}
