package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.sweetbun.becomeanyone.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.dto.auth.LoginRequest;
import ru.sweetbun.becomeanyone.dto.profile.ProfileRequest;
import ru.sweetbun.becomeanyone.dto.user.request.UserRequest;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;
import ru.sweetbun.becomeanyone.entity.Profile;
import ru.sweetbun.becomeanyone.entity.Role;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.UserRepository;
import ru.sweetbun.becomeanyone.util.SecurityUtils;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    private RoleServiceImpl roleServiceImpl;

    @Mock
    private ProfileService profileService;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private TokenService tokenService;

    @Mock
    private TokenBlacklistService tokenBlacklistService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private UserServiceImpl userService;

    private UserResponse userResponse;
    private User user;
    private UserRequest userRequest;
    private ProfileRequest profileRequest;
    private Role role;
    private Profile profile;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, modelMapper, roleServiceImpl,
                profileService, securityUtils, tokenService, tokenBlacklistService, refreshTokenService);

        userRequest = UserRequest.builder().username("user").password("password").build();
        userResponse = UserResponse.builder().id(1L).username("user").build();
        user = User.builder().id(1L).username("user").salt("randomSalt").password("encodedPassword").build();

        role = new Role(1L,"ROLE_STUDENT");

        profileRequest = ProfileRequest.builder().build();
        profile = new Profile();
    }

    @Test
    void register_UserAlreadyExists_ThrowsIllegalArgumentException() {
        // Arrange
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(userRequest)
        );
        assertEquals("User already exists", exception.getMessage());
    }

    @Test
    void authenticate_ValidPassword_ReturnsRoles() {
        // Arrange
        String username = "user";
        String password = "password";
        LoginRequest loginRequest = LoginRequest.builder().username(username).password(password).build();

        user.setSalt("randomSalt");
        user.setPassword(password + "randomSalt");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password + "randomSalt", user.getPassword())).thenReturn(true);
        when(tokenService.generateRefreshToken(anyString(), anyBoolean())).thenReturn("token");

        // Act
        var roles = userService.login(loginRequest, false);

        // Assert
        assertNotNull(roles);
        verify(refreshTokenService).saveRefreshToken(eq(username), anyString(), anyLong());
        verify(tokenService).generateAccessToken(eq(username), anyLong(), anyList());
    }

    @Test
    void authenticate_InvalidPassword_ThrowsUsernameNotFoundException() {
        // Arrange
        String username = "user";
        String password = "wrongPassword";
        LoginRequest loginRequest = LoginRequest.builder().username(username).password(password).build();

        user.setSalt("randomSalt");
        user.setPassword(passwordEncoder.encode("correctPassword" + "randomSalt"));

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password + "randomSalt", user.getPassword())).thenReturn(false);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.login(loginRequest, false));
    }



    @Test
    void getUserById_UserExists_ReturnsUserResponse() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        UserResponse result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getUserById_UserDoesNotExist_ThrowsResourceNotFoundException() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.getUserById(1L)
        );
        assertEquals("User not found with id: 1", exception.getMessage());
    }

    @Test
    void register_ValidUserDTO_UserSavedWithRole() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleServiceImpl.getRoleByName("ROLE_STUDENT")).thenReturn(role);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.register(userRequest);

        assertEquals(1, result.getRoles().size());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_InvalidRole_ThrowsException() {
        when(roleServiceImpl.getRoleByName("ROLE_STUDENT")).thenThrow(ResourceNotFoundException.class);

        assertThrows(ResourceNotFoundException.class, () -> userService.register(userRequest));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void fetchUserById_UserExists_ReturnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.fetchUserById(1L);

        assertEquals(user, result);
    }

    @Test
    void fetchUserById_UserDoesNotExist_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.fetchUserById(1L));
    }

    @Test
    void updateUser_ExistingUser_UpdatesAndSavesUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserResponse result = userService.updateUser(userRequest, 1L);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_NonExistingUser_ThrowsResourceNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userRequest, 1L));
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
        when(profileService.createProfile(profileRequest)).thenReturn(profile);
        when(userRepository.save(user)).thenReturn(user);

        UserResponse result = userService.createUserProfile(profileRequest);

        assertNotNull(result);
        verify(userRepository).save(user);
    }

    @Test
    void createUserProfile_UserWithExistingProfile_ThrowsException() {
        user.setProfile(profile);
        when(securityUtils.getCurrentUser()).thenReturn(user);

        assertThrows(RuntimeException.class, () -> userService.createUserProfile(profileRequest));
        verify(userRepository, never()).save(user);
    }

    @Test
    void getAllUsers_ReturnsListOfUsers() {
        List<User> users = List.of(user, user);
        when(userRepository.findAll()).thenReturn(users);

        List<UserResponse> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
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

        UserResponse result = userService.getCurrentUser();

        assertNotNull(result);
        verify(securityUtils).getCurrentUser();
    }

    @Test
    void updateUserProfile_ValidProfileDTO_UpdatesProfile() {
        user.setProfile(profile);
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(profileService.updateProfile(profileRequest, profile)).thenReturn(profile);
        when(userRepository.save(user)).thenReturn(user);

        UserResponse result = userService.updateUserProfile(profileRequest);

        assertNotNull(result);
        verify(userRepository).save(user);
    }
}