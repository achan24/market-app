package ie.revalue.authenticatedbackend.service;


import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.Conversation;
import ie.revalue.authenticatedbackend.repository.ConversationRepository;
import ie.revalue.authenticatedbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //System.out.println("In the user details service");

        //return if there is a user in the database - checking the database for a user
        //or throw user not found

        //returns optional
        return userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException(
                "user is not valid"));

//        if(!username.equals("Albert")) throw new UsernameNotFoundException("Albert not found");
//
//        Set<Role> roles = new HashSet<>();
//        roles.add(new Role(1, "User"));
//        return new ApplicationUser(1, "Albert", passwordEncoder.encode("password"), roles);
    }

    public ApplicationUser getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public Integer getUserIdByUsername(String username) {
        ApplicationUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return user.getUserId();
    }


    public void addSellerListing(Integer userId, Integer listingId) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.getSellerListingIds().add(listingId);
        userRepository.save(user);
    }

    public void addBuyerListing(Integer userId, Integer listingId) {
        ApplicationUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.getBuyerListingIds().add(listingId);
        userRepository.save(user);
    }

    public Optional<ApplicationUser> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

//    public List<Conversation> getAllConversations() {
//        List<Conversation> allConversations = new ArrayList<>();
//        allConversations.addAll(buyerConversations);
//        allConversations.addAll(sellerConversations);
//        return allConversations;
//    }
    public List<Conversation> getAllConversationsForUser(Integer userId) {
        return conversationRepository.findByBuyerIdOrSellerId(userId, userId);
    }


}
