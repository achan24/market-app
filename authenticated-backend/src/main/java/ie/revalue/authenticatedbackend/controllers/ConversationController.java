package ie.revalue.authenticatedbackend.controllers;

import ie.revalue.authenticatedbackend.exceptions.ResourceNotFoundException;
import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.Conversation;
import ie.revalue.authenticatedbackend.models.Listing;
import ie.revalue.authenticatedbackend.models.Message;
import ie.revalue.authenticatedbackend.service.ConversationService;
import ie.revalue.authenticatedbackend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
@CrossOrigin("*")
public class ConversationController {

    @Autowired
    private ConversationService conversationService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getUserConversations(@AuthenticationPrincipal Jwt jwt, HttpServletRequest request) {
        try {
            String username = jwt.getClaimAsString("sub");
            System.out.println("Username from JWT: " + username);

            // Log request details
            System.out.println("Request URL: " + request.getRequestURL());
            System.out.println("Request Method: " + request.getMethod());
            System.out.println("Request Headers:");
            request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
                System.out.println(headerName + ": " + request.getHeader(headerName));
            });
            System.out.println("Query Parameters:");
            request.getParameterMap().forEach((key, value) -> {
                System.out.println(key + ": " + String.join(", ", value));
            });

            ApplicationUser user = userService.getUserByUsername(username);
            System.out.println("ApplicationUser: " + user);
            List<Conversation> conversations = conversationService.getUserConversations(user);
            System.out.println("Conversations: " + conversations);
            return ResponseEntity.ok(conversations);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
    }

    @GetMapping("/inbox-listings")
    public ResponseEntity<?> getInboxListings(@AuthenticationPrincipal Jwt jwt) {
        try {
            String username = jwt.getClaimAsString("sub");
            ApplicationUser user = userService.getUserByUsername(username);
            List<Listing> inboxListings = conversationService.getInboxListings(user);
            return ResponseEntity.ok(inboxListings);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<?> getConversationMessages(
            @PathVariable Integer conversationId,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            String username = jwt.getClaimAsString("sub");
            ApplicationUser user = userService.getUserByUsername(username);
            List<Message> messages = conversationService.getConversationMessages(conversationId, user);
            return ResponseEntity.ok(messages);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conversation not found");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/listings/{listingId}/conversation")
    public ResponseEntity<?> getListingConversation(
            @PathVariable Integer listingId,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            String username = jwt.getClaimAsString("sub");
            ApplicationUser user = userService.getUserByUsername(username);
            Conversation conversation = conversationService.getListingConversation(listingId, user);
            return ResponseEntity.ok(conversation);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Listing or conversation not found");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/listings/{listingId}/conversation")
    public ResponseEntity<?> createListingConversation(
            @PathVariable Integer listingId,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            String username = jwt.getClaimAsString("sub");
            ApplicationUser user = userService.getUserByUsername(username);
            Conversation conversation = conversationService.createListingConversation(listingId, user);
            return ResponseEntity.ok(conversation);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Listing not found");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }


    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<?> sendMessage(
            @PathVariable Integer conversationId,
            @RequestBody String content,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            String username = jwt.getClaimAsString("sub");
            ApplicationUser user = userService.getUserByUsername(username);
            Message message = conversationService.sendMessage(conversationId, user, content);
            return ResponseEntity.ok(message);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conversation not found");
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
