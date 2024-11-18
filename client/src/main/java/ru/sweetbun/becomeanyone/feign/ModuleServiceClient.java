package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.response.ModuleResponse;

import java.util.List;

@RequestMapping("/courses/{courseId}/modules")
@FeignClient(name = "moduleService", url = "http://localhost:8080")
public interface ModuleServiceClient {

    @PostMapping
    ModuleResponse createModule(@RequestBody CreateModuleRequest moduleDTO, @PathVariable("courseId") Long courseId);

    @GetMapping
    List<ModuleResponse> getAllModulesByCourse(@PathVariable("courseId") Long courseId);

    @GetMapping("{id}")
    ModuleResponse getModuleById(@PathVariable("{id}") Long id);

    @PatchMapping("{id}")
    ModuleResponse updateModule(@RequestBody UpdateModuleRequest updateModuleDTO, @PathVariable("id") Long id);

    @DeleteMapping("{id}")
    long deleteModuleById(@PathVariable("id") Long id);
}
