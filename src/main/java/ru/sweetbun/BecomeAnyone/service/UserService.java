package ru.sweetbun.BecomeAnyone.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    private final ProfileService profileService;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper modelMapper,
                       RoleService roleService, ProfileService profileService) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
        this.profileService = profileService;
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
                .orElseThrow(() -> new ResourceNotFoundException(User.class.getSimpleName(), id));
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
        user = modelMapper.map(userDTO, User.class);
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public User getUserProfile() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserByUsername(username);
    }

    public User createUserProfile(ProfileDTO profileDTO) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = getUserByUsername(username);

        if (user.getProfile() != null) {
            throw new RuntimeException("Profile already exists for this user");
        }
        Profile profile =  profileService.createProfile(profileDTO);

        profile.setUser(user);
        user.setProfile(profile);

        return userRepository.save(user);
    }

    public User updateUserProfile(ProfileDTO profileDTO) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = getUserByUsername(username);

        Profile profile = profileService.updateProfile(profileDTO, user.getProfile().getId());

        user.setProfile(profile);
        profile.setUser(user);

        return userRepository.save(user);
    }
}
