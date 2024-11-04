package ru.sweetbun.BecomeAnyone.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.sweetbun.BecomeAnyone.DTO.ProfileDTO;
import ru.sweetbun.BecomeAnyone.DTO.UserDTO;
import ru.sweetbun.BecomeAnyone.entity.Profile;
import ru.sweetbun.BecomeAnyone.entity.Role;
import ru.sweetbun.BecomeAnyone.entity.User;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.UserRepository;
import ru.sweetbun.BecomeAnyone.util.SecurityUtils;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    private final ProfileService profileService;

    private final SecurityUtils securityUtils;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper,
                       RoleService roleService, ProfileService profileService, @Lazy SecurityUtils securityUtils) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.profileService = profileService;
        this.securityUtils = securityUtils;
    }

    public User register(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role role = roleService.getRoleByName("ROLE_STUDENT");
        log.info("Default role of user: {}", role.getName());
        user.getRoles().add(role);
        return userRepository.save(user);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(User.class, id));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    public User updateUser(UserDTO userDTO, Long id) {
        User user = getUserById(id);
        modelMapper.map(userDTO, user);
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public User getUserProfile() {
        return securityUtils.getCurrentUser();
    }

    public User createUserProfile(ProfileDTO profileDTO) {
        User user = securityUtils.getCurrentUser();

        if (user.getProfile() != null) {
            throw new RuntimeException("Profile already exists for this user");
        }
        Profile profile =  profileService.createProfile(profileDTO);

        profile.setUser(user);
        user.setProfile(profile);

        return userRepository.save(user);
    }

    public User updateUserProfile(ProfileDTO profileDTO) {
        User user = securityUtils.getCurrentUser();
        Profile profile = profileService.updateProfile(profileDTO, user.getProfile().getId());
        user.setProfile(profile);
        profile.setUser(user);
        return userRepository.save(user);
    }
}
