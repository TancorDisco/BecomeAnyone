package ru.sweetbun.becomeanyone.contract;

import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.response.ModuleResponse;

import java.util.List;

public interface ModuleService {

    ModuleResponse createModule(CreateModuleRequest moduleDTO, Long courseId);
    List<ModuleResponse> getAllModulesByCourse(Long courseId);
    ModuleResponse getModuleById(Long id);
    ModuleResponse updateModule(UpdateModuleRequest updateModuleDTO, Long id);
    long deleteModuleById(Long id);
}
