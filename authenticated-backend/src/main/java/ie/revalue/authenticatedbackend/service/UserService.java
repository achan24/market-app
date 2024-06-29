package ie.revalue.authenticatedbackend.service;


import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.Role;
import ie.revalue.authenticatedbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

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
}
