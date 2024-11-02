package ru.sweetbun.BecomeAnyone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.BecomeAnyone.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @Autowired
    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.of(Optional.ofNullable(userService.getAllUsers()));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
