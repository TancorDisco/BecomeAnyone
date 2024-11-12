package ru.sweetbun.become_anyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.become_anyone.entity.User;
import ru.sweetbun.become_anyone.service.UserService;

import java.util.List;

import static java.util.Optional.ofNullable;
import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.of(ofNullable(userService.getAllUsers()));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserProfileById(@PathVariable Long id) {
        return ok(userService.getUserById(id));
    }
}
