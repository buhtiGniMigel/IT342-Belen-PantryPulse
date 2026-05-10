package edu.cit.belen.pantrypulse.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByEmail_ReturnsUser() {
        User user = new User();
        user.setEmail("test@test.com");
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        Optional<User> found = userService.findByEmail("test@test.com");
        assertTrue(found.isPresent());
        assertEquals("test@test.com", found.get().getEmail());
    }

    @Test
    void testFindByEmail_NotFound_ReturnsEmpty() {
        when(userRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());

        Optional<User> found = userService.findByEmail("notfound@test.com");
        assertFalse(found.isPresent());
    }
}
