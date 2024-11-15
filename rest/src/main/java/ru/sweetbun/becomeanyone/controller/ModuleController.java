package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.CreateModuleDTO;
import ru.sweetbun.becomeanyone.dto.UpdateModuleDTO;
import ru.sweetbun.becomeanyone.domain.service.ModuleService;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/courses/{courseId}/modules")
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    public ResponseEntity<?> createModule(@PathVariable("courseId") Long courseId, @RequestBody CreateModuleDTO moduleDTO) {
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
    public ResponseEntity<?> updateModule(@PathVariable("id") Long id, @RequestBody UpdateModuleDTO updateModuleDTO) {
        return ok(moduleService.updateModule(updateModuleDTO, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteModule(@PathVariable("id") Long id) {
        return ok(moduleService.deleteModuleById(id));
    }
}
