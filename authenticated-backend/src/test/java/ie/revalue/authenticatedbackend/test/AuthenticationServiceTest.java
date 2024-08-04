package ie.revalue.authenticatedbackend.test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ie.revalue.authenticatedbackend.models.ApplicationUser;
import ie.revalue.authenticatedbackend.models.Role;
import ie.revalue.authenticatedbackend.repository.RoleRepository;
import ie.revalue.authenticatedbackend.repository.UserRepository;
import ie.revalue.authenticatedbackend.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        // Arrange
        String username = "testuser";
        String password = "password123";
        String encodedPassword = "encodedPassword123";
        Role userRole = new Role(1, "USER");

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(roleRepository.findByAuthority("USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(ApplicationUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ApplicationUser registeredUser = authenticationService.registerUser(username, password);

        // Assert
        assertNotNull(registeredUser);
        assertEquals(username, registeredUser.getUsername());
        assertEquals(encodedPassword, registeredUser.getPassword());
        assertTrue(registeredUser.getAuthorities().stream().anyMatch(role -> role.getAuthority().equals("USER")));

        // Verify
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).encode(password);
        verify(roleRepository).findByAuthority("USER");
        verify(userRepository).save(any(ApplicationUser.class));
    }
}