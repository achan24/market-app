package ie.revalue.authenticatedbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.UserDetailsDTO;
import ie.revalue.authenticatedbackend.repository.UserRepository;
import ie.revalue.authenticatedbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin("*")
public class UserController {

    @GetMapping("/")
    public String helloUserController() {
        return "User access level";
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserDetails(@PathVariable String username, @AuthenticationPrincipal Jwt jwt) {
        String tokenUsername = jwt.getClaimAsString("sub");
        System.out.println("JWT token username: " + tokenUsername);
        System.out.println("Requested username: " + username);

        if (!tokenUsername.equals(username)) {
            System.out.println("Access denied: JWT token username does not match the requested username.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }


        try {
            ApplicationUser user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            System.out.println("user: " + user);


            UserDetailsDTO userDetails = new UserDetailsDTO(
                    user.getUsername(),
                    user.getEmail(),
                    user.getLocation(),
                    user.getCreatedAt()
            );
            System.out.println("user details: " + userDetails);

            String jsonResponse = new ObjectMapper().writeValueAsString(userDetails);
            System.out.println("Sending response: " + jsonResponse);

            return ResponseEntity.ok(userDetails);
        } catch (UsernameNotFoundException e) {
            System.out.println("User not found: " + username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + username);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error fetching user details: " + e.getMessage());
        }
    }
}
