package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.contract.ModuleService;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.response.ModuleResponse;
import ru.sweetbun.becomeanyone.feign.ModuleServiceClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModuleServiceImpl implements ModuleService {

    private final ModuleServiceClient moduleServiceClient;

    @Override
    public ModuleResponse createModule(CreateModuleRequest moduleDTO, Long courseId) {
        return moduleServiceClient.createModule(moduleDTO, courseId);
    }

    @Override
    public List<ModuleResponse> getAllModulesByCourse(Long courseId) {
        return moduleServiceClient.getAllModulesByCourse(courseId);
    }

    @Override
    public ModuleResponse getModuleById(Long id) {
        return moduleServiceClient.getModuleById(id);
    }

    @Override
    public ModuleResponse updateModule(UpdateModuleRequest updateModuleDTO, Long id) {
        return moduleServiceClient.updateModule(updateModuleDTO, id);
    }

    @Override
    public long deleteModuleById(Long id) {
        return moduleServiceClient.deleteModuleById(id);
    }
}
