package ru.sweetbun.BecomeAnyone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.BecomeAnyone.DTO.ModuleDTO;
import ru.sweetbun.BecomeAnyone.service.ModuleService;

@RestController
@RequestMapping("/courses/{courseId}/modules")
public class ModuleController {

    private final ModuleService moduleService;

    @Autowired
    public ModuleController(ModuleService moduleService) {
        this.moduleService = moduleService;
    }

    @PostMapping
    public ResponseEntity<?> createModule(@PathVariable("courseId") Long courseId, @RequestBody ModuleDTO moduleDTO) {
        return ResponseEntity.ok(moduleService.createModule(moduleDTO, courseId));
    }

    @GetMapping
    public ResponseEntity<?> getAllModulesByCourse(@PathVariable("courseId") Long courseId) {
        return ResponseEntity.ok(moduleService.getAllModulesByCourse(courseId));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getModuleById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(moduleService.getModuleById(id));
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateModule(@PathVariable("id") Long id, @RequestBody ModuleDTO moduleDTO) {
        return ResponseEntity.ok(moduleService.updateModule(moduleDTO, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteModule(@PathVariable("id") Long id) {
        return ResponseEntity.ok(moduleService.deleteModuleById(id));
    }
}
