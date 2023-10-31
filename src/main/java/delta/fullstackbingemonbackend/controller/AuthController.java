package delta.fullstackbingemonbackend.controller;

import delta.fullstackbingemonbackend.model.User;
import delta.fullstackbingemonbackend.payload.JsonWebToken;
import delta.fullstackbingemonbackend.payload.LoginRequest;
import delta.fullstackbingemonbackend.payload.SignupRequest;
import delta.fullstackbingemonbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    private UserService userService;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private JsonWebToken jsonWebToken;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JsonWebToken jsonWebToken) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jsonWebToken = jsonWebToken;
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

        User user = new User(signupRequest.getUsername(), passwordEncoder.encode(signupRequest.getPassword()), signupRequest.getEmail());
        userService.saveUser(user);

        return ResponseEntity.ok("User signup was successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jsonWebToken.generateJWT(loginRequest.getUsername());

        System.out.println("What does the loginResponse contains?: " + token);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, "user=" + token).body("Login was successful");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        response.setHeader(HttpHeaders.SET_COOKIE, "user=; Max-Age=0");
        return ResponseEntity.ok("You've been logged out");
    }

}
