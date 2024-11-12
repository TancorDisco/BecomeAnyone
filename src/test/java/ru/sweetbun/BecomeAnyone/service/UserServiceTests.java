package ru.sweetbun.BecomeAnyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sweetbun.BecomeAnyone.DTO.UserDTO;
import ru.sweetbun.BecomeAnyone.config.ModelMapperConfig;
import ru.sweetbun.BecomeAnyone.repository.UserRepository;
import ru.sweetbun.BecomeAnyone.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import ru.sweetbun.BecomeAnyone.DTO.ProfileDTO;
import ru.sweetbun.BecomeAnyone.entity.Profile;
import ru.sweetbun.BecomeAnyone.entity.Role;
import ru.sweetbun.BecomeAnyone.entity.User;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private RoleService roleService;

    @Mock
    private ProfileService profileService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserDTO userDTO;
    private ProfileDTO profileDTO;
    private Role role;
    private Profile profile;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder, modelMapper, roleService,
                profileService, securityUtils);

        userDTO = UserDTO.builder().username("password").build();

        user = User.builder().id(1L).password("encodedPassword").build();

        role = new Role(1L,"ROLE_STUDENT");

        profileDTO = ProfileDTO.builder().build();
        profile = new Profile();
    }

    @Test
    void register_ValidUserDTO_UserSavedWithRole() {
        when(passwordEncoder.encode(userDTO.password())).thenReturn("encodedPassword");
        when(roleService.getRoleByName("ROLE_STUDENT")).thenReturn(role);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.register(userDTO);

        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getRoles().contains(role));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_InvalidRole_ThrowsException() {
        when(roleService.getRoleByName("ROLE_STUDENT")).thenThrow(new RuntimeException("Role not found"));

        assertThrows(RuntimeException.class, () -> userService.register(userDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals(user, result);
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
    }

    @Test
    void updateUser_ExistingUser_UpdatesAndSavesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUser(userDTO, 1L);

        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_NonExistingUser_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userDTO, 1L));
    }

    @Test
    void deleteUserById_UserExists_DeletesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        long deletedUserId = userService.deleteUserById(1L);

        assertEquals(1L, deletedUserId);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserById_UserDoesNotExist_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserById(1L));
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void createUserProfile_UserWithoutProfile_CreatesProfile() {
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(profileService.createProfile(profileDTO)).thenReturn(profile);
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.createUserProfile(profileDTO);

        assertEquals(profile, result.getProfile());
        verify(userRepository).save(user);
    }

    @Test
    void createUserProfile_UserWithExistingProfile_ThrowsException() {
        user.setProfile(profile);
        when(securityUtils.getCurrentUser()).thenReturn(user);

        assertThrows(RuntimeException.class, () -> userService.createUserProfile(profileDTO));
        verify(userRepository, never()).save(user);
    }

    @Test
    void getAllUsers_ReturnsListOfUsers() {
        List<User> users = List.of(user, user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(users, result);
        verify(userRepository).findAll();
    }

    @Test
    void getUserByUsername_UserExists_ReturnsUser() {
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername("username");

        assertEquals(user, result);
    }

    @Test
    void getUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByUsername("username"));
    }

    @Test
    void getCurrentUser_ReturnsCurrentCurrentUser() {
        when(securityUtils.getCurrentUser()).thenReturn(user);

        User result = userService.getCurrentUser();

        assertEquals(user, result);
        verify(securityUtils).getCurrentUser();
    }

    @Test
    void updateUserProfile_ValidProfileDTO_UpdatesProfile() {
        user.setProfile(profile);
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(profileService.updateProfile(profileDTO, profile)).thenReturn(profile);
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.updateUserProfile(profileDTO);

        assertEquals(profile, result.getProfile());
        verify(userRepository).save(user);
    }
}