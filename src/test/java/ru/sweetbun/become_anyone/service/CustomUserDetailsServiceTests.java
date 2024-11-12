package ru.sweetbun.become_anyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.sweetbun.become_anyone.entity.Role;
import ru.sweetbun.become_anyone.entity.User;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTests {

    @Mock
    private UserService userService;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role(1L, "ROLE_USER");
        user = User.builder().username("user").password("user").roles(Set.of(role)).build();
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        String username = "user";
        when(userService.getUserByUsername(username)).thenReturn(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
        verify(userService, times(1)).getUserByUsername(username);
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsUsernameNotFoundException() {
        String username = "user";
        when(userService.getUserByUsername(username)).thenThrow(new UsernameNotFoundException("User not found"));

        assertThrows(UsernameNotFoundException.class, () -> customUserDetailsService.loadUserByUsername(username));
        verify(userService, times(1)).getUserByUsername(username);
    }

    @Test
    void loadUserByUsername_UserWithNoRoles_ReturnsUserDetailsWithEmptyAuthorities() {
        String username = "user";
        user.setRoles(Collections.emptySet());
        when(userService.getUserByUsername(username)).thenReturn(user);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(0, userDetails.getAuthorities().size());
        verify(userService, times(1)).getUserByUsername(username);
    }
}