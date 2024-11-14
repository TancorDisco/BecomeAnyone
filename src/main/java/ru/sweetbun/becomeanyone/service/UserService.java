package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.dto.ProfileDTO;
import ru.sweetbun.becomeanyone.dto.UserDTO;
import ru.sweetbun.becomeanyone.entity.Profile;
import ru.sweetbun.becomeanyone.entity.Role;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.UserRepository;
import ru.sweetbun.becomeanyone.util.SecurityUtils;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final RoleService roleService;

    private final ProfileService profileService;
    @Lazy
    private final SecurityUtils securityUtils;

    @Transactional
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

    @Transactional
    public User updateUser(UserDTO userDTO, Long id) {
        User user = getUserById(id);
        modelMapper.map(userDTO, user);
        return userRepository.save(user);
    }

    @Transactional
    public long deleteUserById(Long id) {
        getUserById(id);
        userRepository.deleteById(id);
        return id;
    }

    public User getCurrentUser() {
        return securityUtils.getCurrentUser();
    }

    @Transactional
    public User createUserProfile(ProfileDTO profileDTO) {
        User user = securityUtils.getCurrentUser();
        if (user.getProfile() != null)
            throw new IllegalArgumentException("Profile already exists for this user");

        Profile profile =  profileService.createProfile(profileDTO);
        profile.setUser(user);
        user.setProfile(profile);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserProfile(ProfileDTO profileDTO) {
        User user = securityUtils.getCurrentUser();
        Profile profile = profileService.updateProfile(profileDTO, user.getProfile());
        user.setProfile(profile);
        profile.setUser(user);
        return userRepository.save(user);
    }
}
