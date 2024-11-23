package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.becomeanyone.contract.RoleService;

import static org.springframework.http.ResponseEntity.ok;

@RequestMapping("/users/{userId}/roles")
@RestController
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PutMapping("/teacher")
    public ResponseEntity<?> appointTeacher(@PathVariable Long userId) {
        return ok(roleService.appointTeacher(userId));
    }

    @PutMapping("/admin")
    public ResponseEntity<?> appointAdmin(@PathVariable Long userId) {
        return ok(roleService.appointAdmin(userId));
    }
}
