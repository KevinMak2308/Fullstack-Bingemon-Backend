package delta.fullstackbingemonbackend.controller;

import delta.fullstackbingemonbackend.model.User;
import delta.fullstackbingemonbackend.payload.SignupRequest;
import delta.fullstackbingemonbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
    private UserService userService;

    private PasswordEncoder passwordEncoder;

    @Autowired
    public AuthController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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

        User user = new User(signupRequest.getUsername(), passwordEncoder.encode(signupRequest.getPassword()));
        userService.saveUser(user);

        return ResponseEntity.ok("User signup was successful");
    }

}
