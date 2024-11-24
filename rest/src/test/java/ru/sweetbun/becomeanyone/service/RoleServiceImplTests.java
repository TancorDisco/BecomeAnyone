package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.sweetbun.becomeanyone.contract.UserService;
import ru.sweetbun.becomeanyone.dto.role.RoleRequest;
import ru.sweetbun.becomeanyone.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.entity.Role;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTests {

    @Mock
    private RoleRepository roleRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private UserServiceImpl userService;

    @InjectMocks
    private RoleServiceImpl roleServiceImpl;

    private Role role;

    @BeforeEach
    void setUp() {
        roleServiceImpl = new RoleServiceImpl(userService, roleRepository, modelMapper);

        role = new Role();
    }

    @Test
    void createRole_ValidRoleDTO_ShouldReturnSavedRole() {
        RoleRequest roleRequest = new RoleRequest("ROLE_ADMIN");
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role savedRole = roleServiceImpl.createRole(roleRequest);

        assertNotNull(savedRole);
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void getRoleById_ExistingId_ShouldReturnRole() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));

        Role foundRole = roleServiceImpl.getRoleById(1L);

        assertNotNull(foundRole);
        assertEquals(role, foundRole);
    }

    @Test
    void getRoleById_NonExistingId_ShouldThrowException() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleServiceImpl.getRoleById(1L));
    }

    @Test
    void getAllRoles_WhenRolesExist_ShouldReturnListOfRoles() {
        List<Role> roles = List.of(role, role);
        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> foundRoles = roleServiceImpl.getAllRoles();

        assertNotNull(foundRoles);
        assertEquals(2, foundRoles.size());
    }

    @Test
    void updateRole_ExistingId_ShouldReturnUpdatedRole() {
        RoleRequest roleRequest = new RoleRequest("");

        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));
        when(roleRepository.save(role)).thenReturn(role);

        Role updatedRole = roleServiceImpl.updateRole(roleRequest, 1L);

        assertNotNull(updatedRole);
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void updateRole_NonExistingId_ShouldThrowException() {
        RoleRequest roleRequest = new RoleRequest("");
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleServiceImpl.updateRole(roleRequest, 1L));
    }

    @Test
    void deleteRoleById_ExistingId_ShouldReturnId() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));
        doNothing().when(roleRepository).deleteById(anyLong());

        long deletedId = roleServiceImpl.deleteRoleById(1L);

        assertEquals(1L, deletedId);
        verify(roleRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteRoleById_NonExistingId_ShouldThrowException() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleServiceImpl.deleteRoleById(1L));
    }

    @Test
    void getRoleByName_ExistingName_ShouldReturnRole() {
        String roleName = "ROLE_ADMIN";
        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));

        Role foundRole = roleServiceImpl.getRoleByName(roleName);

        assertNotNull(foundRole);
        assertEquals(role, foundRole);
    }

    @Test
    void getRoleByName_NonExistingName_ShouldThrowException() {
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleServiceImpl.getRoleByName("ROLE_UNKNOWN"));
    }
}