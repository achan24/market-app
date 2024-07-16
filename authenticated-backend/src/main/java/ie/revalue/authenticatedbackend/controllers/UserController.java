package ie.revalue.authenticatedbackend.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.Conversation;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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


            List<Conversation> userConversations = userService.getAllConversationsForUser(user.getUserId());


            UserDetailsDTO userDetails = new UserDetailsDTO(
                    user.getUsername(),
                    user.getEmail(),
                    user.getLocation(),
                    user.getCreatedAt(),
                    user.getSellerListingIds(),
                    user.getBuyerListingIds(),
                    userConversations,
                    user.getProfilePic()
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


    @PutMapping("/{username}")
    public ResponseEntity<?> updateUserDetails(
            @PathVariable String username,
            @RequestParam Map<String, String> updates,
            @RequestParam(value = "profilePic", required = false) MultipartFile profilePic,
            @AuthenticationPrincipal Jwt jwt) {
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
            System.out.println("User before update: " + user);

            String newEmail = updates.get("email");
            String newLocation = updates.get("location");

            if (newEmail != null) {
                user.setEmail(newEmail);
            }
            if (newLocation != null) {
                user.setLocation(newLocation);
            }

            if (profilePic != null && !profilePic.isEmpty()) {
                user.setProfilePic(profilePic.getBytes());
            }

            userRepository.save(user);
            System.out.println("User after update: " + user);

            List<Conversation> userConversations = userService.getAllConversationsForUser(user.getUserId());

            UserDetailsDTO userDetails = new UserDetailsDTO(
                    user.getUsername(),
                    user.getEmail(),
                    user.getLocation(),
                    user.getCreatedAt(),
                    user.getSellerListingIds(),
                    user.getBuyerListingIds(),
                    userConversations,
                    user.getProfilePic()
            );

            return ResponseEntity.ok(userDetails);
        } catch (UsernameNotFoundException e) {
            System.out.println("User not found: " + username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + username);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error updating user details: " + e.getMessage());
        }
    }



    @GetMapping("/email/{username}")
    public ResponseEntity<?> getUserEmail(@PathVariable String username, @AuthenticationPrincipal Jwt jwt) {
        String tokenUsername = jwt.getClaimAsString("sub");
        System.out.println("JWT token username: " + tokenUsername);
        System.out.println("Requested username: " + username);

        // Allow fetching email if the user is authenticated
        if (tokenUsername == null) {
            System.out.println("Access denied: No JWT token provided.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        try {
            ApplicationUser user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            return ResponseEntity.ok(Collections.singletonMap("email", user.getEmail()));
        } catch (UsernameNotFoundException e) {
            System.out.println("User not found: " + username);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + username);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error fetching user email: " + e.getMessage());
        }
    }


}
