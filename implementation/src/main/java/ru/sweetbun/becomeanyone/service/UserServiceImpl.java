package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.dto.profile.ProfileRequest;
import ru.sweetbun.becomeanyone.dto.user.UserRequest;
import ru.sweetbun.becomeanyone.contract.AuthService;
import ru.sweetbun.becomeanyone.contract.ProfileService;
import ru.sweetbun.becomeanyone.contract.UserService;
import ru.sweetbun.becomeanyone.domain.entity.Profile;
import ru.sweetbun.becomeanyone.domain.entity.Role;
import ru.sweetbun.becomeanyone.domain.entity.User;
import ru.sweetbun.becomeanyone.dto.user.UserResponse;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.domain.repository.UserRepository;
import ru.sweetbun.becomeanyone.util.SecurityUtils;

import java.util.List;

@org.springframework.context.annotation.Profile("default")
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@Service
public class UserServiceImpl implements UserService, ProfileService, AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    private final ru.sweetbun.becomeanyone.service.ProfileService profileService;
    @Lazy
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public UserResponse register(UserRequest userRequest) {
        User user = modelMapper.map(userRequest, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleService.getRoleByName("ROLE_STUDENT");
        log.info("Default role of user: {}", role.getName());
        user.getRoles().add(role);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponse.class);
    }


    public User fetchUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, id));
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = fetchUserById(id);
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .toList();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    @Override
    @Transactional
    public UserResponse updateUser(UserRequest userRequest, Long id) {
        User user = fetchUserById(id);
        modelMapper.map(userRequest, user);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Override
    @Transactional
    public long deleteUserById(Long id) {
        fetchUserById(id);
        userRepository.deleteById(id);
        return id;
    }

    @Override
    public UserResponse getCurrentUser() {
        return modelMapper.map(securityUtils.getCurrentUser(), UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse createUserProfile(ProfileRequest profileRequest) {
        User user = securityUtils.getCurrentUser();
        if (user.getProfile() != null)
            throw new IllegalArgumentException("Profile already exists for this user");

        Profile profile =  profileService.createProfile(profileRequest);
        profile.setUser(user);
        user.setProfile(profile);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse updateUserProfile(ProfileRequest profileRequest) {
        User user = securityUtils.getCurrentUser();
        Profile profile = profileService.updateProfile(profileRequest, user.getProfile());
        user.setProfile(profile);
        profile.setUser(user);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserResponse.class);
    }
}
