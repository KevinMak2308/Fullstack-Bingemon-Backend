package delta.fullstackbingemonbackend.service;

import delta.fullstackbingemonbackend.model.User;
import delta.fullstackbingemonbackend.payload.UserDetailsAuthentication;
import delta.fullstackbingemonbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceAuthentication implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    public UserDetailsServiceAuthentication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("No user with that username exist");
        }
        return new UserDetailsAuthentication(user.getUsername(), user.getPassword());
    }
}
