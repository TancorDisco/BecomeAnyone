package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.contract.RoleService;
import ru.sweetbun.becomeanyone.contract.UserService;
import ru.sweetbun.becomeanyone.dto.role.RoleRequest;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;
import ru.sweetbun.becomeanyone.entity.Role;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.RoleRepository;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService {

    @Lazy
    private final UserServiceImpl userServiceImpl;

    private final RoleRepository roleRepository;

    protected final ModelMapper modelMapper;

    @Transactional
    @Override
    public UserResponse appointTeacher(Long userId) {
        User user = userServiceImpl.fetchUserById(userId);
        Role role = getRoleByName("ROLE_TEACHER");
        user.getRoles().add(role);
        return modelMapper.map(user, UserResponse.class);
    }

    @Transactional
    @Override
    public UserResponse appointAdmin(Long userId) {
        User user = userServiceImpl.fetchUserById(userId);
        Role role = getRoleByName("ROLE_ADMIN");
        user.getRoles().add(role);
        return modelMapper.map(user, UserResponse.class);
    }

    @Transactional
    public Role createRole(RoleRequest roleRequest) {
        Role role = modelMapper.map(roleRequest, Role.class);
        return roleRepository.save(role);
    }

    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Role.class, id));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Transactional
    public Role updateRole(RoleRequest roleRequest, Long id) {
        Role role = getRoleById(id);
        modelMapper.map(roleRequest, role);
        return roleRepository.save(role);
    }

    @Transactional
    public long deleteRoleById(Long id) {
        getRoleById(id);
        roleRepository.deleteById(id);
        return id;
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(Role.class, name));
    }
}
