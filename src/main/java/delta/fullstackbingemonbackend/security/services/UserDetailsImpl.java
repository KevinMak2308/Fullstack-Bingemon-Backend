package delta.fullstackbingemonbackend.security.services;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
@Setter
public class UserDetailsImpl implements UserDetails {

    @NonNull
    private Long id;
    @NonNull
    private String username;

    @NonNull
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl() {}

    //Can add authorties to the constructor if we want to create multiple roles for each user login in
    //Example authorities.add(new CustomAuthority("user"));
    //Example authorities.add(new CustomAuthority("admin"));
    //Example authorities.add(new CustomAuthority("premium"));
    public UserDetailsImpl(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public Collection<? extends GrantedAuthority> getGrantedAuthorities() {
        return authorities;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
