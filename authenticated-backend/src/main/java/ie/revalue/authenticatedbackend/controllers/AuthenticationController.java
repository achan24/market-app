package ie.revalue.authenticatedbackend.controllers;

import ie.revalue.authenticatedbackend.exceptions.UsernameAlreadyExistsException;
import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.LoginResponseDTO;
import ie.revalue.authenticatedbackend.models.RegistrationDTO;
import ie.revalue.authenticatedbackend.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*") //not best practices
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationDTO body) {
        try {
            ApplicationUser user = authenticationService.registerUser(body.getUsername(), body.getPassword());
            return ResponseEntity.ok(user);
        } catch (UsernameAlreadyExistsException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "An unexpected error occurred"));
        }
    }



    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody RegistrationDTO body) {

        try {
            LoginResponseDTO loginResponse = authenticationService.loginUser(body.getUsername(), body.getPassword());
            return ResponseEntity.ok(loginResponse);
        } catch (BadCredentialsException e) {
            // Make sure the message is non-null and informative
            String errorMessage = "Invalid username or password";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", errorMessage));
        } catch (UsernameNotFoundException e) {
            String errorMessage = "User not found";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", errorMessage));
        } catch (LockedException | DisabledException e) {
            // Include specific error message based on the exception
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            String errorMessage = Optional.ofNullable(e.getMessage()).orElse("An unexpected error occurred");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", errorMessage));
        }

    }
}
