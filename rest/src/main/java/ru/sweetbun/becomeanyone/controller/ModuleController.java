package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.contract.ModuleService;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleRequest;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "Module Management", description = "API для управления модулями курса")
@RequiredArgsConstructor
@RestController
@RequestMapping("/courses/{courseId}/modules")
public class ModuleController {

    private final ModuleService moduleService;

    @PostMapping
    @Operation(summary = "Создать новый модуль", description = "Создает отдельно новый модуль для указанного курса (без уроков)")
    public ResponseEntity<?> createModule(@PathVariable("courseId") Long courseId, @RequestBody CreateModuleRequest request) {
        return ok(moduleService.createModule(request, courseId));
    }

    @GetMapping
    @Operation(summary = "Получить все модули курса", description = "Возвращает все модули, связанные с конкретным курсом")
    public ResponseEntity<?> getAllModulesByCourse(@PathVariable("courseId") Long courseId) {
        return ok(moduleService.getAllModulesByCourse(courseId));
    }

    @GetMapping("{id}")
    @Operation(summary = "Получить модуль по ID", description = "Получает конкретный модуль по его ID")
    public ResponseEntity<?> getModuleById(@PathVariable("id") Long id) {
        return ok(moduleService.getModuleById(id));
    }

    @PatchMapping("{id}")
    @Operation(summary = "Обновить модуль", description = "Обновляет отдельно конкретный модуль по его ID (без уроков)")
    public ResponseEntity<?> updateModule(@PathVariable("id") Long id, @RequestBody UpdateModuleRequest request) {
        return ok(moduleService.updateModule(request, id));
    }

    @DeleteMapping("{id}")
    @Operation(summary = "Удалить модуль", description = "Удаляет конкретный модуль по его ID")
    public ResponseEntity<?> deleteModule(@PathVariable("id") Long id) {
        return ok(moduleService.deleteModuleById(id));
    }
}
