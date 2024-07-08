package ie.revalue.authenticatedbackend.service;

import ie.revalue.authenticatedbackend.exceptions.UsernameAlreadyExistsException;
import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.LoginResponseDTO;
import ie.revalue.authenticatedbackend.models.Role;
import ie.revalue.authenticatedbackend.repository.RoleRepository;
import ie.revalue.authenticatedbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.AuthenticationException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@Transactional
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public ApplicationUser registerUser(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UsernameAlreadyExistsException("Username already taken");
        }
        String encodedPassword = passwordEncoder.encode(password);
        Role userRole = roleRepository.findByAuthority("USER").get(); //returns optional

        Set<Role> authorities = new HashSet<>();  //make a set of roles for our authorities

        authorities.add(userRole);

        //create new user and store inside database

        return userRepository.save(
                new ApplicationUser(
                        0,
                        username,
                        encodedPassword,
                        null,
                        null,
                        LocalDateTime.now(),
                        LocalDateTime.now(),
                        null,
                        null,
                        null,
                        null,
                        authorities
                )
        );
    }

    public LoginResponseDTO loginUser(String username, String password) {

        try {
            System.out.println("inside loginUser - attempting to authenticate " + username);
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            System.out.println("passed authentication manager");

            String token = tokenService.generateJwt(auth);
            ApplicationUser user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return new LoginResponseDTO(user, token);
        } catch (AuthenticationException e) { // Catch all types of authentication-related exceptions
            System.out.println("Authentication failed for user: " + username + ". Error: " + e.getMessage());
            throw e; // Rethrow the exception
        }

    }

}
