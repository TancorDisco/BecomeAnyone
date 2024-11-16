package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.dto.role.RoleRequest;
import ru.sweetbun.becomeanyone.entity.Role;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.RoleRepository;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    protected final ModelMapper modelMapper;

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
