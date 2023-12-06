package delta.fullstackbingemonbackend.controller;

import delta.fullstackbingemonbackend.model.User;
import delta.fullstackbingemonbackend.repository.UserRepository;
import delta.fullstackbingemonbackend.security.jwt.JsonWebToken;
import delta.fullstackbingemonbackend.security.payload.LoginRequest;
import delta.fullstackbingemonbackend.security.payload.SignupRequest;
import delta.fullstackbingemonbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("api/auth")
public class AuthController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JsonWebToken jsonWebToken;
    private final UserRepository userRepository;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JsonWebToken jsonWebToken,
                          @Qualifier("User") UserRepository userRepository) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jsonWebToken = jsonWebToken;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest signupRequest) {

        if (userService.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists");
        }

        //password validation/regex implementeres her
        if (signupRequest.getPassword() == null) {
            return ResponseEntity.badRequest().body("Password is null");
        }

        User user = new User(signupRequest.getUsername(), signupRequest.getName(), passwordEncoder.encode(signupRequest.getPassword()), signupRequest.getEmail());
        userService.saveUser(user);

        return ResponseEntity.ok("User signup was successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String token = jsonWebToken.generateJWT(loginRequest.getUsername());


            User user = userRepository.findByUsername(loginRequest.getUsername());


            System.out.println("What does the loginResponse contains?: " + token);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Login was successful");
            response.put("token", token);
            response.put("user_id", user.getId().toString());
            return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).header(HttpHeaders.AUTHORIZATION, "user_id=" + user.getId().toString()).body(response);
        } catch (Exception error) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication Failed: " + error.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        response.addHeader(HttpHeaders.SET_COOKIE, "user=; Max-Age=0");
        response.addHeader(HttpHeaders.SET_COOKIE, "user_id=; Max-Age=0");
        return ResponseEntity.ok("You've been logged out");
    }

}
