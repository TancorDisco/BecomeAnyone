package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sweetbun.becomeanyone.dto.role.RoleResponse;
import ru.sweetbun.becomeanyone.entity.Profile;
import ru.sweetbun.becomeanyone.entity.Role;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.util.SecurityUtils;
import ru.sweetbun.becomeanyone.dto.profile.ProfileRequest;
import ru.sweetbun.becomeanyone.dto.user.request.UserRequest;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;
import ru.sweetbun.becomeanyone.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.UserRepository;

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

    private UserResponse userResponse;
    private User user;
    private UserRequest userRequest;
    private ProfileRequest profileRequest;
    private Role role;
    private Profile profile;

    @BeforeEach
    void setUp() {
        userServiceImpl = new UserServiceImpl(userRepository, passwordEncoder, modelMapper, roleService,
                profileService, securityUtils);

        userRequest = UserRequest.builder().username("password").build();
        userResponse = UserResponse.builder().id(1L).build();
        user = new User();

        role = new Role(1L,"ROLE_STUDENT");

        profileRequest = ProfileRequest.builder().build();
        profile = new Profile();
    }

    @Test
    void register_ValidUserDTO_UserSavedWithRole() {
        when(passwordEncoder.encode(userRequest.password())).thenReturn("encodedPassword");
        when(roleService.getRoleByName("ROLE_STUDENT")).thenReturn(role);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userServiceImpl.register(userRequest);

        assertEquals(1, result.getRoles().size());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_InvalidRole_ThrowsException() {
        when(roleService.getRoleByName("ROLE_STUDENT")).thenThrow(new RuntimeException("Role not found"));

        assertThrows(RuntimeException.class, () -> userServiceImpl.register(userRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void fetchUserById_UserExists_ReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userServiceImpl.fetchUserById(1L);

        assertEquals(user, result);
    }

    @Test
    void fetchUserById_UserDoesNotExist_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userServiceImpl.fetchUserById(1L));
    }

    @Test
    void updateUser_ExistingUser_UpdatesAndSavesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse result = userServiceImpl.updateUser(userRequest, 1L);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_NonExistingUser_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userServiceImpl.updateUser(userRequest, 1L));
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
        when(profileService.createProfile(profileRequest)).thenReturn(profile);
        when(userRepository.save(user)).thenReturn(user);

        UserResponse result = userServiceImpl.createUserProfile(profileRequest);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void createUserProfile_UserWithExistingProfile_ThrowsException() {
        user.setProfile(profile);
        when(securityUtils.getCurrentUser()).thenReturn(user);

        assertThrows(RuntimeException.class, () -> userServiceImpl.createUserProfile(profileRequest));
        verify(userRepository, never()).save(user);
    }

    @Test
    void getAllUsers_ReturnsListOfUsers() {
        List<User> users = List.of(user, user);
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> result = userServiceImpl.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
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

        UserResponse result = userServiceImpl.getCurrentUser();

        assertNotNull(result);
        verify(securityUtils).getCurrentUser();
    }

    @Test
    void updateUserProfile_ValidProfileDTO_UpdatesProfile() {
        user.setProfile(profile);
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(profileService.updateProfile(profileRequest, profile)).thenReturn(profile);
        when(userRepository.save(user)).thenReturn(user);

        UserResponse result = userServiceImpl.updateUserProfile(profileRequest);

        assertNotNull(result);
        verify(userRepository).save(user);
    }
}