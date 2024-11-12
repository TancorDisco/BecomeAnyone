package ru.sweetbun.BecomeAnyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.sweetbun.BecomeAnyone.DTO.CreateModuleDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateModuleDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateModuleInCourseDTO;
import ru.sweetbun.BecomeAnyone.config.ModelMapperConfig;
import ru.sweetbun.BecomeAnyone.entity.Course;
import ru.sweetbun.BecomeAnyone.entity.Module;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.ModuleRepository;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ModuleServiceTests {

    @Mock
    private LessonService lessonService;

    @Mock
    private ModuleRepository moduleRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private CourseService courseService;

    @InjectMocks
    private ModuleService moduleService;

    private Course course;
    private Map<Long, Module> currentModulesMap;

    @BeforeEach
    public void setUp() {
        moduleService = new ModuleService(moduleRepository, lessonService, modelMapper, courseService);

        course = Course.builder().id(1L).build();
        currentModulesMap = new HashMap<>();

        Module module1 = Module.builder().id(1L).title("Module 1").orderNum(1).course(course).build();
        Module module2 = Module.builder().id(2L).title("Module 2").orderNum(2).course(course).build();
        Module module3 = Module.builder().id(3L).title("Module 3").orderNum(3).course(course).build();

        currentModulesMap.put(1L, module1);
        currentModulesMap.put(2L, module2);
        currentModulesMap.put(3L, module3);
    }

    @Test
    public void createModule_ValidInput_ModuleCreated() {
        CreateModuleDTO moduleDTO = new CreateModuleDTO();
        when(courseService.getCourseById(anyLong())).thenReturn(course);
        when(moduleRepository.save(any(Module.class))).thenAnswer(i -> i.getArguments()[0]);

        Module result = moduleService.createModule(moduleDTO, course.getId());

        assertNotNull(result);
        assertEquals(course, result.getCourse());
        verify(moduleRepository).save(any(Module.class));
    }

    @Test
    public void createModule_NonexistentCourse_ThrowsResourceNotFoundException() {
        CreateModuleDTO moduleDTO = new CreateModuleDTO();
        when(courseService.getCourseById(anyLong())).thenThrow(new ResourceNotFoundException(Course.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> moduleService.createModule(moduleDTO, 1L));
    }

    @Test
    public void getModuleById_ExistingId_ReturnsModule() {
        Module module = currentModulesMap.get(1L);
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(module));

        Module result = moduleService.getModuleById(1L);

        assertEquals(module, result);
    }

    @Test
    public void getModuleById_NonexistentId_ThrowsResourceNotFoundException() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> moduleService.getModuleById(1L));
    }

    @Test
    public void updateModule_ValidInput_ModuleUpdated() {
        UpdateModuleDTO updateDTO = UpdateModuleDTO.builder().build();
        Module module = currentModulesMap.get(1L);
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.of(module));
        when(moduleRepository.save(any(Module.class))).thenReturn(module);

        Module result = moduleService.updateModule(updateDTO, 1L);

        assertEquals(module, result);
        verify(moduleRepository).save(module);
    }

    @Test
    public void updateModule_NonexistentId_ThrowsResourceNotFoundException() {
        UpdateModuleDTO updateDTO = UpdateModuleDTO.builder().build();
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> moduleService.updateModule(updateDTO, 1L));
    }

    @Test
    public void deleteModuleById_ExistingId_ModuleDeleted() {
        Module module = currentModulesMap.get(1L);
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.of(module));
        when(moduleRepository.findByOrderNumGreaterThan(module.getOrderNum()))
                .thenReturn(List.of(currentModulesMap.get(2L), currentModulesMap.get(3L)));

        long result = moduleService.deleteModuleById(1L);

        assertEquals(1L, result);
        verify(moduleRepository).deleteById(1L);
        assertEquals(1, currentModulesMap.get(2L).getOrderNum());
        assertEquals(2, currentModulesMap.get(3L).getOrderNum());
    }

    @Test
    public void deleteModuleById_NonexistentId_ThrowsResourceNotFoundException() {
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> moduleService.deleteModuleById(1L));
    }

    @Test
    public void updateModules_ValidInput_ModulesUpdated() {
        List<UpdateModuleInCourseDTO> updateDTOs = List.of(new UpdateModuleInCourseDTO(), new UpdateModuleInCourseDTO());
        course.setModules(List.of(currentModulesMap.get(1L), currentModulesMap.get(2L)));
        when(moduleRepository.save(any(Module.class))).thenAnswer(i -> i.getArguments()[0]);

        List<Module> result = moduleService.updateModules(updateDTOs, course);

        assertNotNull(result);
        assertEquals(updateDTOs.size(), result.size());
        verify(moduleRepository, times(1)).deleteAll(any());
    }

    @Test
    public void updateModules_EmptyList_NoModulesUpdatedOrDeleted() {
        List<UpdateModuleInCourseDTO> emptyUpdateDTOs = new ArrayList<>();

        List<Module> result = moduleService.updateModules(emptyUpdateDTOs, course);

        assertTrue(result.isEmpty());
        verify(moduleRepository, never()).deleteAll(anyList());
    }

    @Test
    public void getAllModulesByCourse_ExistingCourseId_ReturnsSortedModules() {
        Long courseId = 1L;
        List<Module> modules = List.of(currentModulesMap.get(1L), currentModulesMap.get(2L));
        when(courseService.getCourseById(courseId)).thenReturn(new Course());
        when(moduleRepository.findAllByCourseOrderByOrderNumAsc(any(Course.class))).thenReturn(modules);

        List<Module> result = moduleService.getAllModulesByCourse(courseId);

        assertEquals(modules.size(), result.size());
        assertTrue(result.get(0).getOrderNum() < result.get(1).getOrderNum());
    }

    @Test
    public void getAllModulesByCourse_NonexistentCourseId_ThrowsResourceNotFoundException() {
        when(courseService.getCourseById(anyLong())).thenThrow(new ResourceNotFoundException(Course.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> moduleService.getAllModulesByCourse(1L));
    }

    @Test
    public void createModules_ValidInput_ModulesCreated() {
        List<CreateModuleDTO> moduleDTOs = List.of(new CreateModuleDTO(), new CreateModuleDTO());
        when(moduleRepository.save(any(Module.class))).thenAnswer(i -> i.getArguments()[0]);

        moduleService.createModules(moduleDTOs, course);

        verify(moduleRepository, times(moduleDTOs.size())).save(any(Module.class));
        verify(lessonService, times(moduleDTOs.size())).createLessons(anyList(), any(Module.class));
    }

    @Test
    public void createModules_EmptyList_NoModulesCreated() {
        List<CreateModuleDTO> emptyModuleDTOs = new ArrayList<>();

        moduleService.createModules(emptyModuleDTOs, course);

        verify(moduleRepository, never()).save(any(Module.class));
        verify(lessonService, never()).createLessons(anyList(), any(Module.class));
    }

    @DisplayName("MergeModules with various scenarios")
    @ParameterizedTest(name = "{index} => moduleDTOS={0}, countUpdatedModules={1}, " +
            "expectedTitles={2}, countModulesForDeletion={3}")
    @MethodSource("moduleScenariosProvider")
    public void mergeModules_VariousScenarios(List<UpdateModuleInCourseDTO> moduleDTOS,
                                              int countUpdatedModules,
                                              List<String> expectedTitles,
                                              int countModulesForDeletion) {
        //Arrange
        lenient().when(moduleRepository.save(any(Module.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(lessonService.updateLessons(anyList(), any(Module.class))).thenReturn(new ArrayList<>());

        // Act
        List<Module> updatedModules = ModuleService.mergeModules(moduleDTOS, modelMapper, currentModulesMap, course,
                moduleRepository, lessonService);

        // Assert
        assertEquals(countUpdatedModules, updatedModules.size());
        for (int i = 0; i < countUpdatedModules; i++) {
            assertEquals(expectedTitles.get(i), updatedModules.get(i).getTitle());
        }
        assertEquals(countModulesForDeletion, currentModulesMap.size());

        verify(moduleRepository, times(countUpdatedModules)).save(any(Module.class));
        verify(lessonService, times(countUpdatedModules)).updateLessons(anyList(), any(Module.class));
    }

    private Stream<Arguments> moduleScenariosProvider() {
        return Stream.of(
                Arguments.of( // 1
                        List.of(
                                UpdateModuleInCourseDTO.builder().id(1L).title("Updated Module 1").build(),
                                UpdateModuleInCourseDTO.builder().id(2L).title("Updated Module 2").build()
                        ),
                        2,
                        List.of("Updated Module 1", "Updated Module 2"),
                        1
                ),
                Arguments.of( // 2
                        List.of(
                                UpdateModuleInCourseDTO.builder().title("New Module 1").build(),
                                UpdateModuleInCourseDTO.builder().title("New Module 2").build()
                        ),
                        2,
                        List.of("New Module 1", "New Module 2"),
                        3
                ),
                Arguments.of( // 3
                        List.of(
                                UpdateModuleInCourseDTO.builder().id(1L).title("Updated Module 1").build(),
                                UpdateModuleInCourseDTO.builder().title("New Module 3").build(),
                                UpdateModuleInCourseDTO.builder().id(2L).title("Updated Module 2").build()
                        ),
                        3,
                        List.of("Updated Module 1", "New Module 3", "Updated Module 2"),
                        1
                ),
                Arguments.of( // 4
                        Collections.emptyList(),
                        0,
                        Collections.emptyList(),
                        3
                ),
                Arguments.of( // 5
                        List.of(
                                UpdateModuleInCourseDTO.builder().id(1L).title("Updated Module 1").build(),
                                UpdateModuleInCourseDTO.builder().title("New Module 3").build()
                        ),
                        2,
                        List.of("Updated Module 1", "New Module 3"),
                        2
                ),
                Arguments.of(
                        List.of(
                                UpdateModuleInCourseDTO.builder().id(1L).title(null).build(),
                                UpdateModuleInCourseDTO.builder().id(3L).title(null).build()
                        ),
                        2,
                        List.of("Module 1", "Module 3"),
                        1
                )
        );
    }
}