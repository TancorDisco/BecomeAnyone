package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
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
import ru.sweetbun.becomeanyone.api.dto.CreateModuleDTO;
import ru.sweetbun.becomeanyone.api.dto.UpdateModuleDTO;
import ru.sweetbun.becomeanyone.api.dto.UpdateModuleInCourseDTO;
import ru.sweetbun.becomeanyone.domain.service.CourseService;
import ru.sweetbun.becomeanyone.domain.service.LessonService;
import ru.sweetbun.becomeanyone.domain.service.ModuleService;
import ru.sweetbun.becomeanyone.infrastructure.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.domain.entity.Course;
import ru.sweetbun.becomeanyone.domain.entity.Module;
import ru.sweetbun.becomeanyone.infrastructure.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.infrastructure.repository.ModuleRepository;

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
    void createModule_ValidInput_ModuleCreated() {
        CreateModuleDTO moduleDTO = new CreateModuleDTO();
        when(courseService.getCourseById(anyLong())).thenReturn(course);
        when(moduleRepository.save(any(Module.class))).thenAnswer(i -> i.getArguments()[0]);

        Module result = moduleService.createModule(moduleDTO, course.getId());

        assertNotNull(result);
        assertEquals(course, result.getCourse());
        verify(moduleRepository).save(any(Module.class));
    }

    @Test
    void createModule_NonexistentCourse_ThrowsResourceNotFoundException() {
        CreateModuleDTO moduleDTO = new CreateModuleDTO();
        when(courseService.getCourseById(anyLong())).thenThrow(new ResourceNotFoundException(Course.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> moduleService.createModule(moduleDTO, 1L));
    }

    @Test
    void getModuleById_ExistingId_ReturnsModule() {
        Module module = currentModulesMap.get(1L);
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(module));

        Module result = moduleService.getModuleById(1L);

        assertEquals(module, result);
    }

    @Test
    void getModuleById_NonexistentId_ThrowsResourceNotFoundException() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> moduleService.getModuleById(1L));
    }

    @Test
    void updateModule_ValidInput_ModuleUpdated() {
        UpdateModuleDTO updateDTO = UpdateModuleDTO.builder().build();
        Module module = currentModulesMap.get(1L);
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.of(module));
        when(moduleRepository.save(any(Module.class))).thenReturn(module);

        Module result = moduleService.updateModule(updateDTO, 1L);

        assertEquals(module, result);
        verify(moduleRepository).save(module);
    }

    @Test
    void updateModule_NonexistentId_ThrowsResourceNotFoundException() {
        UpdateModuleDTO updateDTO = UpdateModuleDTO.builder().build();
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> moduleService.updateModule(updateDTO, 1L));
    }

    @Test
    void deleteModuleById_ExistingId_ModuleDeleted() {
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
    void deleteModuleById_NonexistentId_ThrowsResourceNotFoundException() {
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> moduleService.deleteModuleById(1L));
    }

    @Test
    void updateModules_ValidInput_ModulesUpdated() {
        List<UpdateModuleInCourseDTO> updateDTOs = List.of(new UpdateModuleInCourseDTO(), new UpdateModuleInCourseDTO());
        course.setModules(List.of(currentModulesMap.get(1L), currentModulesMap.get(2L)));
        when(moduleRepository.save(any(Module.class))).thenAnswer(i -> i.getArguments()[0]);

        List<Module> result = moduleService.updateModules(updateDTOs, course);

        assertNotNull(result);
        assertEquals(updateDTOs.size(), result.size());
        verify(moduleRepository, times(1)).deleteAll(any());
    }

    @Test
    void updateModules_EmptyList_NoModulesUpdatedOrDeleted() {
        List<UpdateModuleInCourseDTO> emptyUpdateDTOs = new ArrayList<>();

        List<Module> result = moduleService.updateModules(emptyUpdateDTOs, course);

        assertTrue(result.isEmpty());
        verify(moduleRepository, never()).deleteAll(anyList());
    }

    @Test
    void getAllModulesByCourse_ExistingCourseId_ReturnsSortedModules() {
        Long courseId = 1L;
        List<Module> modules = List.of(currentModulesMap.get(1L), currentModulesMap.get(2L));
        when(courseService.getCourseById(courseId)).thenReturn(new Course());
        when(moduleRepository.findAllByCourseOrderByOrderNumAsc(any(Course.class))).thenReturn(modules);

        List<Module> result = moduleService.getAllModulesByCourse(courseId);

        assertEquals(modules.size(), result.size());
        assertTrue(result.get(0).getOrderNum() < result.get(1).getOrderNum());
    }

    @Test
    void getAllModulesByCourse_NonexistentCourseId_ThrowsResourceNotFoundException() {
        when(courseService.getCourseById(anyLong())).thenThrow(new ResourceNotFoundException(Course.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> moduleService.getAllModulesByCourse(1L));
    }

    @Test
    void createModules_ValidInput_ModulesCreated() {
        List<CreateModuleDTO> moduleDTOs = List.of(new CreateModuleDTO(), new CreateModuleDTO());
        when(moduleRepository.save(any(Module.class))).thenAnswer(i -> i.getArguments()[0]);

        moduleService.createModules(moduleDTOs, course);

        verify(moduleRepository, times(moduleDTOs.size())).save(any(Module.class));
        verify(lessonService, times(moduleDTOs.size())).createLessons(anyList(), any(Module.class));
    }

    @Test
    void createModules_EmptyList_NoModulesCreated() {
        List<CreateModuleDTO> emptyModuleDTOs = new ArrayList<>();

        moduleService.createModules(emptyModuleDTOs, course);

        verify(moduleRepository, never()).save(any(Module.class));
        verify(lessonService, never()).createLessons(anyList(), any(Module.class));
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void testUpdateModules(List<UpdateModuleInCourseDTO> moduleDTOS, List<Module> expectedModules) {
        // Arrange
        course.setModules(new ArrayList<>(currentModulesMap.values()));

        when(moduleRepository.save(any(Module.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(lessonService.updateLessons(any(), any())).thenReturn(List.of());

        // Act
        List<Module> updatedModules = moduleService.updateModules(moduleDTOS, course);

        // Assert
        assertEquals(expectedModules.size(), updatedModules.size());
        for (int i = 0; i < expectedModules.size(); i++) {
            assertEquals(expectedModules.get(i).getTitle(), updatedModules.get(i).getTitle());
            assertEquals(expectedModules.get(i).getOrderNum(), updatedModules.get(i).getOrderNum());
        }

        verify(moduleRepository, times(expectedModules.size())).save(any(Module.class));
        verify(lessonService, times(moduleDTOS.size())).updateLessons(any(), any());
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                // 1: Add new modules
                Arguments.of(
                        List.of(
                                UpdateModuleInCourseDTO.builder().id(1L).title("Module 1").orderNum(1).build(),
                                UpdateModuleInCourseDTO.builder().id(2L).title("Module 2").orderNum(2).build(),
                                UpdateModuleInCourseDTO.builder().id(3L).title("Module 3").orderNum(3).build(),
                                UpdateModuleInCourseDTO.builder().title("New Module 1").orderNum(4).build(),
                                UpdateModuleInCourseDTO.builder().title("New Module 2").orderNum(5).build()
                        ),
                        List.of(
                                Module.builder().id(1L).title("Module 1").orderNum(1).build(),
                                Module.builder().id(2L).title("Module 2").orderNum(2).build(),
                                Module.builder().id(3L).title("Module 3").orderNum(3).build(),
                                Module.builder().id(4L).title("New Module 1").orderNum(4).build(),
                                Module.builder().id(5L).title("New Module 2").orderNum(5).build()
                        )
                ),
                // 2: Update existing modules
                Arguments.of(
                        List.of(
                                UpdateModuleInCourseDTO.builder().id(1L).title("Updated Module 1").orderNum(1).build(),
                                UpdateModuleInCourseDTO.builder().id(2L).title("Updated Module 2").orderNum(2).build(),
                                UpdateModuleInCourseDTO.builder().id(3L).title("Module 3").orderNum(3).build()
                        ),
                        List.of(
                                Module.builder().id(1L).title("Updated Module 1").orderNum(1).build(),
                                Module.builder().id(2L).title("Updated Module 2").orderNum(2).build(),
                                Module.builder().id(3L).title("Module 3").orderNum(3).build()
                        )
                ),
                // 3: Add new and update existing modules
                Arguments.of(
                        List.of(
                                UpdateModuleInCourseDTO.builder().id(1L).title("Updated Module 1").orderNum(1).build(),
                                UpdateModuleInCourseDTO.builder().id(2L).title("Module 2").orderNum(2).build(),
                                UpdateModuleInCourseDTO.builder().id(3L).title("Module 3").orderNum(3).build(),
                                UpdateModuleInCourseDTO.builder().title("New Module 3").orderNum(4).build()
                        ),
                        List.of(
                                Module.builder().id(1L).title("Updated Module 1").orderNum(1).build(),
                                Module.builder().id(2L).title("Module 2").orderNum(2).build(),
                                Module.builder().id(3L).title("Module 3").orderNum(3).build(),
                                Module.builder().id(4L).title("New Module 3").orderNum(4).build()
                        )
                ),
                // 4: Add new and delete old
                Arguments.of(
                        List.of(
                                UpdateModuleInCourseDTO.builder().title("New Module 1").orderNum(4).build(),
                                UpdateModuleInCourseDTO.builder().title("New Module 2").orderNum(5).build()
                        ),
                        List.of(
                                Module.builder().id(4L).title("New Module 1").orderNum(4).build(),
                                Module.builder().id(5L).title("New Module 2").orderNum(5).build()
                        )
                ),
                // 5: Update existing modules and delete old
                Arguments.of(
                        List.of(
                                UpdateModuleInCourseDTO.builder().id(1L).title("Updated Module 1").orderNum(1).build(),
                                UpdateModuleInCourseDTO.builder().id(2L).title("Updated Module 2").orderNum(2).build()
                        ),
                        List.of(
                                Module.builder().id(1L).title("Updated Module 1").orderNum(1).build(),
                                Module.builder().id(2L).title("Updated Module 2").orderNum(2).build()
                        )
                ),
                // 6: Add new and update existing modules and delete old
                Arguments.of(
                        List.of(
                                UpdateModuleInCourseDTO.builder().id(1L).title("Updated Module 1").orderNum(1).build(),
                                UpdateModuleInCourseDTO.builder().title("New Module 3").orderNum(4).build()
                        ),
                        List.of(
                                Module.builder().id(1L).title("Updated Module 1").orderNum(1).build(),
                                Module.builder().id(4L).title("New Module 3").orderNum(4).build()
                        )
                )
        );
    }
}