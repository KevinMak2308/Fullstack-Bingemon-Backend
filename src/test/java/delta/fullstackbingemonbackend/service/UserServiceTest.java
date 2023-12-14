package delta.fullstackbingemonbackend.service;

import delta.fullstackbingemonbackend.model.User;
import delta.fullstackbingemonbackend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;

    @Test
    void saveUser() {
        // Step 1 - Arrange test subjects
        User userIsa = new User("isab554b", "Isa", "isakode12", "isa@email.com");
        userIsa.setId(1L);
        when(userRepository.save(userIsa)).thenReturn(userIsa);

        // Step 2 - Act out the test sequence
        User savedUser = userService.saveUser(userIsa);

        // Step 3 - Assert the expected result against the final result
        assertEquals("isab554b", savedUser.getUsername());
        assertEquals("Isa", savedUser.getName());
        assertEquals("isakode12", savedUser.getPassword());
        assertEquals("isa@email.com", savedUser.getEmail());

        verify(userRepository, times(1)).save(userIsa);
    }

    @Test
    void findById() {
        // Step 1 - Arrange test subjects
        User userIsa = new User("isab554b", "Isa", "isakode12", "isa@email.com");
        userIsa.setId(3L);
        when(userRepository.findById(userIsa.getId())).thenReturn(Optional.of(userIsa));

        // Step 2 - Act out the test sequence
        User foundUser = userService.findById(userIsa.getId());

        // Step 3 - Assert the expected result against the final result
        assertEquals(3, foundUser.getId());
        assertEquals("isab554b", foundUser.getUsername());

        verify(userRepository, times(1)).findById(userIsa.getId());
    }

    @Test
    void existsByUsername() {
        // Step 1 - Arrange test subjects
        User userIsa = new User("isab554b", "Isa", "isakode12", "isa@email.com");
        when(userRepository.existsByUsername(userIsa.getUsername())).thenReturn(true);

        // Step 2 - Act out the test sequence
        Boolean existingUser = userService.existsByUsername(userIsa.getUsername());

        // Step 3 - Assert the expected result against the final result
        assertTrue(existingUser);

        verify(userRepository, times(1)).existsByUsername(userIsa.getUsername());
    }

    @Test
    void updateUser() {
        // Step 1 - Arrange test subjects
        User userIsa = new User("isab554b", "Isa", "isakode12", "isa@email.com");
        userIsa.setId(3L);
        when(userRepository.findById(userIsa.getId())).thenReturn(Optional.of(userIsa));
        when(userRepository.save(userIsa)).thenReturn(userIsa);

        // Step 2 - Act out the test sequence
        userIsa.setUsername("isac665c");
        userIsa.setName("Isabella");
        userIsa.setPassword("isakode345");
        userIsa.setEmail("isabella@email.com");

        User updatedUser = userService.updateUser(userIsa, userIsa.getId());

        // Step 3 - Assert the expected result against the final result
        assertEquals("isac665c", updatedUser.getUsername());
        assertEquals("Isabella", updatedUser.getName());
        assertEquals("isakode345", updatedUser.getPassword());
        assertEquals("isabella@email.com", updatedUser.getEmail());

        verify(userRepository, times(1)).findById(userIsa.getId());
        verify(userRepository, times(1)).save(userIsa);
    }

    @Test
    void deleteUser() {
        // Step 1 - Arrange test subjects
        User userIsa = new User("isab554b", "Isa", "isakode12", "isa@email.com");
        userIsa.setId(1L);

        // Step 2 - Act out the test sequence
        userService.deleteUser(userIsa.getId());

        // Step 3 - Assert the expected result against the final result
        verify(userRepository, times(1)).deleteById(userIsa.getId());
    }

}