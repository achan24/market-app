package ie.revalue.authenticatedbackend;

import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.Role;
import ie.revalue.authenticatedbackend.repository.RoleRepository;
import ie.revalue.authenticatedbackend.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class AuthenticatedBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticatedBackendApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(RoleRepository roleRepository,
                                 UserRepository userRepository,
                                 PasswordEncoder passwordEncoder) {

        return args -> {
            //check if this has already run
            if(roleRepository.findByAuthority("ADMIN").isPresent())
                return;
            Role adminRole = roleRepository.save(new Role("ADMIN"));
            roleRepository.save(new Role("USER"));

            Set<Role> roles = new HashSet<>();
            roles.add(adminRole);

            ApplicationUser admin = new ApplicationUser(
                    1,
                    "admin",
                    passwordEncoder.encode("password"),
                    "admin@revalue.ie",
                    "Ireland",
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    null,
                    null,
                    null,
                    null,
                    roles);

            userRepository.save(admin);
        };
    }
}
