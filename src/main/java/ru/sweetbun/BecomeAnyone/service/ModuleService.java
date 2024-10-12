package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sweetbun.BecomeAnyone.DTO.ModuleDTO;
import ru.sweetbun.BecomeAnyone.entity.Module;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.ModuleRepository;

import java.util.List;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public ModuleService(ModuleRepository moduleRepository, ModelMapper modelMapper) {
        this.moduleRepository = moduleRepository;
        this.modelMapper = modelMapper;
    }

    public Module createModule(ModuleDTO moduleDTO) {
        Module module = modelMapper.map(moduleDTO, Module.class);
        return moduleRepository.save(module);
    }

    public Module getModuleById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Module.class.getSimpleName(), id));
    }

    public List<Module> getAllModules() {
        return moduleRepository.findAll();
    }

    public Module updateModule(ModuleDTO moduleDTO, Long id) {
        Module module = getModuleById(id);
        module = modelMapper.map(moduleDTO, Module.class);
        return moduleRepository.save(module);
    }

    public void deleteModuleById(Long id) {
        moduleRepository.deleteById(id);
    }
}
