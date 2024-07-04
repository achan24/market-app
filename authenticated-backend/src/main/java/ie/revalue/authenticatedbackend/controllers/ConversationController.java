package ie.revalue.authenticatedbackend.controllers;

import ie.revalue.authenticatedbackend.exceptions.ResourceNotFoundException;
import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.ConversationTopic;
import ie.revalue.authenticatedbackend.models.Message;
import ie.revalue.authenticatedbackend.service.MessageService;
import ie.revalue.authenticatedbackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
public class ConversationController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

//    @GetMapping("/conversations")
//    public ResponseEntity<?> getUserConversations(@AuthenticationPrincipal Jwt jwt) {
//        try {
//            String username = jwt.getClaimAsString("sub");  // Assuming 'sub' claim contains the username
//            ApplicationUser user = userService.getUserByUsername(username);
//            List<ConversationTopic> conversations = messageService.getUserConversations(user);
//            return ResponseEntity.ok(conversations);
//        } catch (UsernameNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
//        }
//    }
//
//    @GetMapping("/conversations/{topicId}")
//    public ResponseEntity<?> getConversationMessages(@PathVariable Integer topicId, @AuthenticationPrincipal Jwt jwt) {
//        try {
//            List<Message> messages = messageService.getConversationMessages(topicId);
//            return ResponseEntity.ok(messages);
//        } catch (UsernameNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
//        } catch (ResourceNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conversation not found");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
//        }
//    }
//
//    @PostMapping("/conversations/{topicId}")
//    public ResponseEntity<?> sendMessage(
//            @PathVariable Integer topicId,
//            @RequestBody String content,
//            @AuthenticationPrincipal Jwt jwt) {
//        try {
//            String username = jwt.getClaimAsString("sub");
//            ApplicationUser user = userService.getUserByUsername(username);
//            Message message = messageService.sendMessage(topicId, user, content);
//            return ResponseEntity.ok(message);
//        } catch (UsernameNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
//        } catch (ResourceNotFoundException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Conversation not found");
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
//        }
//    }
}
