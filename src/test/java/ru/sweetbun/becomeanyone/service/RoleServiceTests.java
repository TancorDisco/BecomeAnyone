package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.sweetbun.becomeanyone.api.dto.RoleDTO;
import ru.sweetbun.becomeanyone.domain.service.RoleService;
import ru.sweetbun.becomeanyone.infrastructure.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.domain.entity.Role;
import ru.sweetbun.becomeanyone.infrastructure.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.infrastructure.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTests {

    @Mock
    private RoleRepository roleRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @InjectMocks
    private RoleService roleService;

    private Role role;

    @BeforeEach
    void setUp() {
        roleService = new RoleService(roleRepository, modelMapper);

        role = new Role();
    }

    @Test
    void createRole_ValidRoleDTO_ShouldReturnSavedRole() {
        RoleDTO roleDTO = new RoleDTO("ROLE_ADMIN");
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role savedRole = roleService.createRole(roleDTO);

        assertNotNull(savedRole);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void getRoleById_ExistingId_ShouldReturnRole() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));

        Role foundRole = roleService.getRoleById(1L);

        assertNotNull(foundRole);
        assertEquals(role, foundRole);
    }

    @Test
    void getRoleById_NonExistingId_ShouldThrowException() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleById(1L));
    }

    @Test
    void getAllRoles_WhenRolesExist_ShouldReturnListOfRoles() {
        List<Role> roles = List.of(role, role);
        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> foundRoles = roleService.getAllRoles();

        assertNotNull(foundRoles);
        assertEquals(2, foundRoles.size());
    }

    @Test
    void updateRole_ExistingId_ShouldReturnUpdatedRole() {
        RoleDTO roleDTO = new RoleDTO("");

        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(role);

        Role updatedRole = roleService.updateRole(roleDTO, 1L);

        assertNotNull(updatedRole);
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void updateRole_NonExistingId_ShouldThrowException() {
        RoleDTO roleDTO = new RoleDTO("");
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.updateRole(roleDTO, 1L));
    }

    @Test
    void deleteRoleById_ExistingId_ShouldReturnId() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));
        doNothing().when(roleRepository).deleteById(anyLong());

        long deletedId = roleService.deleteRoleById(1L);

        assertEquals(1L, deletedId);
        verify(roleRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteRoleById_NonExistingId_ShouldThrowException() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.deleteRoleById(1L));
    }

    @Test
    void getRoleByName_ExistingName_ShouldReturnRole() {
        String roleName = "ROLE_ADMIN";
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));

        Role foundRole = roleService.getRoleByName(roleName);

        assertNotNull(foundRole);
        assertEquals(role, foundRole);
    }

    @Test
    void getRoleByName_NonExistingName_ShouldThrowException() {
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleByName("ROLE_UNKNOWN"));
    }
}