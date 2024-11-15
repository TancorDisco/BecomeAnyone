package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleRequest;
import ru.sweetbun.becomeanyone.service.ModuleService;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/courses/{courseId}/modules")
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<?> createModule(@PathVariable("courseId") Long courseId, @RequestBody CreateModuleRequest moduleDTO) {
        return ok(moduleService.createModule(moduleDTO, courseId));
    }

    @GetMapping
    public ResponseEntity<?> getAllModulesByCourse(@PathVariable("courseId") Long courseId) {
        return ok(moduleService.getAllModulesByCourse(courseId));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getModuleById(@PathVariable("id") Long id) {
        return ok(moduleService.getModuleById(id));
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateModule(@PathVariable("id") Long id, @RequestBody UpdateModuleRequest updateModuleDTO) {
        return ok(moduleService.updateModule(updateModuleDTO, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteModule(@PathVariable("id") Long id) {
        return ok(moduleService.deleteModuleById(id));
    }
}
