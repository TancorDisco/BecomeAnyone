package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sweetbun.becomeanyone.api.dto.UserDTO;
import ru.sweetbun.becomeanyone.domain.service.ProfileService;
import ru.sweetbun.becomeanyone.domain.service.RoleService;
import ru.sweetbun.becomeanyone.domain.service.UserServiceImpl;
import ru.sweetbun.becomeanyone.infrastructure.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.infrastructure.repository.UserRepository;
import ru.sweetbun.becomeanyone.domain.util.SecurityUtils;
import org.junit.jupiter.api.Test;
import ru.sweetbun.becomeanyone.api.dto.ProfileDTO;
import ru.sweetbun.becomeanyone.domain.entity.Profile;
import ru.sweetbun.becomeanyone.domain.entity.Role;
import ru.sweetbun.becomeanyone.domain.entity.User;
import ru.sweetbun.becomeanyone.infrastructure.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {

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
    private UserServiceImpl userServiceImpl;

    private User user;
    private UserDTO userDTO;
    private ProfileDTO profileDTO;
    private Role role;
    private Profile profile;

    @BeforeEach
    void setUp() {
        userServiceImpl = new UserServiceImpl(userRepository, passwordEncoder, modelMapper, roleService,
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

        User result = userServiceImpl.register(userDTO);

        assertEquals("encodedPassword", result.getPassword());
        assertTrue(result.getRoles().contains(role));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_InvalidRole_ThrowsException() {
        when(roleService.getRoleByName("ROLE_STUDENT")).thenThrow(new RuntimeException("Role not found"));

        assertThrows(RuntimeException.class, () -> userServiceImpl.register(userDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userServiceImpl.getUserById(1L);

        assertEquals(user, result);
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userServiceImpl.getUserById(1L));
    }

    @Test
    void updateUser_ExistingUser_UpdatesAndSavesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        User result = userServiceImpl.updateUser(userDTO, 1L);

        assertEquals(user, result);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_NonExistingUser_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userServiceImpl.updateUser(userDTO, 1L));
    }

    @Test
    void deleteUserById_UserExists_DeletesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        long deletedUserId = userServiceImpl.deleteUserById(1L);

        assertEquals(1L, deletedUserId);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserById_UserDoesNotExist_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userServiceImpl.deleteUserById(1L));
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    void createUserProfile_UserWithoutProfile_CreatesProfile() {
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(profileService.createProfile(profileDTO)).thenReturn(profile);
        when(userRepository.save(user)).thenReturn(user);

        User result = userServiceImpl.createUserProfile(profileDTO);

        assertEquals(profile, result.getProfile());
        verify(userRepository).save(user);
    }

    @Test
    void createUserProfile_UserWithExistingProfile_ThrowsException() {
        user.setProfile(profile);
        when(securityUtils.getCurrentUser()).thenReturn(user);

        assertThrows(RuntimeException.class, () -> userServiceImpl.createUserProfile(profileDTO));
        verify(userRepository, never()).save(user);
    }

    @Test
    void getAllUsers_ReturnsListOfUsers() {
        List<User> users = List.of(user, user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userServiceImpl.getAllUsers();

        assertEquals(2, result.size());
        assertEquals(users, result);
        verify(userRepository).findAll();
    }

    @Test
    void getUserByUsername_UserExists_ReturnsUser() {
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        User result = userServiceImpl.getUserByUsername("username");

        assertEquals(user, result);
    }

    @Test
    void getUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userServiceImpl.getUserByUsername("username"));
    }

    @Test
    void getCurrentUser_ReturnsCurrentCurrentUser() {
        when(securityUtils.getCurrentUser()).thenReturn(user);

        User result = userServiceImpl.getCurrentUser();

        assertEquals(user, result);
        verify(securityUtils).getCurrentUser();
    }

    @Test
    void updateUserProfile_ValidProfileDTO_UpdatesProfile() {
        user.setProfile(profile);
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(profileService.updateProfile(profileDTO, profile)).thenReturn(profile);
        when(userRepository.save(user)).thenReturn(user);

        User result = userServiceImpl.updateUserProfile(profileDTO);

        assertEquals(profile, result.getProfile());
        verify(userRepository).save(user);
    }
}