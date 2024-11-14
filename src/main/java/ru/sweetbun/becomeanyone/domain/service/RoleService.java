package ru.sweetbun.becomeanyone.domain.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.api.dto.RoleDTO;
import ru.sweetbun.becomeanyone.domain.entity.Role;
import ru.sweetbun.becomeanyone.infrastructure.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.infrastructure.repository.RoleRepository;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;

    protected final ModelMapper modelMapper;

    @Transactional
    public Role createRole(RoleDTO roleDTO) {
        Role role = modelMapper.map(roleDTO, Role.class);
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
    public Role updateRole(RoleDTO roleDTO, Long id) {
        Role role = getRoleById(id);
        modelMapper.map(roleDTO, role);
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
