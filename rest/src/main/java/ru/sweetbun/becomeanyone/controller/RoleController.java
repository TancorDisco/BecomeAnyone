package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.becomeanyone.contract.RoleService;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "Role Management", description = "API для управления ролями")
@RequestMapping("/users/{userId}/roles")
@RestController
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/teacher")
    @Operation(summary = "Назначить пользователя учителем", description = "Добавляет новую роль пользователю")
    public ResponseEntity<?> appointTeacher(@PathVariable Long userId) {
        return ok(roleService.appointTeacher(userId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin")
    @Operation(summary = "Назначить пользователя админом", description = "Добавляет новую роль пользователю")
    public ResponseEntity<?> appointAdmin(@PathVariable Long userId) {
        return ok(roleService.appointAdmin(userId));
    }
}
