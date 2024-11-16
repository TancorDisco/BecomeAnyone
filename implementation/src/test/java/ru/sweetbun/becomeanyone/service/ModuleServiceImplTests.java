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
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;
import ru.sweetbun.becomeanyone.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.Module;
import ru.sweetbun.becomeanyone.dto.module.response.ModuleResponse;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.ModuleRepository;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ModuleServiceImplTests {

    @Mock
    private LessonServiceImpl lessonServiceImpl;

    @Mock
    private ModuleRepository moduleRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private CourseServiceImpl courseServiceImpl;

    @InjectMocks
    private ModuleServiceImpl moduleServiceImpl;

    private Course course;
    private Map<Long, Module> currentModulesMap;

    @BeforeEach
    public void setUp() {
        moduleServiceImpl = new ModuleServiceImpl(moduleRepository, lessonServiceImpl, modelMapper, courseServiceImpl);

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
        CreateModuleRequest moduleDTO = new CreateModuleRequest();
        when(courseServiceImpl.fetchCourseById(anyLong())).thenReturn(course);
        when(moduleRepository.save(any(Module.class))).thenAnswer(i -> i.getArguments()[0]);

        ModuleResponse result = moduleServiceImpl.createModule(moduleDTO, course.getId());

        assertNotNull(result);
        verify(moduleRepository).save(any(Module.class));
    }

    @Test
    void createModule_NonexistentCourse_ThrowsResourceNotFoundException() {
        CreateModuleRequest moduleDTO = new CreateModuleRequest();
        when(courseServiceImpl.fetchCourseById(anyLong())).thenThrow(new ResourceNotFoundException(Course.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> moduleServiceImpl.createModule(moduleDTO, 1L));
    }

    @Test
    void fetchModuleById_ExistingId_ReturnsModule() {
        Module module = currentModulesMap.get(1L);
        when(moduleRepository.findById(1L)).thenReturn(Optional.of(module));

        Module result = moduleServiceImpl.fetchModuleById(1L);

        assertEquals(module, result);
    }

    @Test
    void fetchModuleById_NonexistentId_ThrowsResourceNotFoundException() {
        when(moduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> moduleServiceImpl.fetchModuleById(1L));
    }

    @Test
    void updateModule_ValidInput_ModuleUpdated() {
        UpdateModuleRequest updateDTO = UpdateModuleRequest.builder().build();
        Module module = currentModulesMap.get(1L);
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.of(module));
        when(moduleRepository.save(any(Module.class))).thenReturn(module);

        ModuleResponse result = moduleServiceImpl.updateModule(updateDTO, 1L);

        assertEquals(module, result);
        verify(moduleRepository).save(module);
    }

    @Test
    void updateModule_NonexistentId_ThrowsResourceNotFoundException() {
        UpdateModuleRequest updateDTO = UpdateModuleRequest.builder().build();
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> moduleServiceImpl.updateModule(updateDTO, 1L));
    }

    @Test
    void deleteModuleById_ExistingId_ModuleDeleted() {
        Module module = currentModulesMap.get(1L);
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.of(module));
        when(moduleRepository.findByOrderNumGreaterThan(module.getOrderNum()))
                .thenReturn(List.of(currentModulesMap.get(2L), currentModulesMap.get(3L)));

        long result = moduleServiceImpl.deleteModuleById(1L);

        assertEquals(1L, result);
        verify(moduleRepository).deleteById(1L);
        assertEquals(1, currentModulesMap.get(2L).getOrderNum());
        assertEquals(2, currentModulesMap.get(3L).getOrderNum());
    }

    @Test
    void deleteModuleById_NonexistentId_ThrowsResourceNotFoundException() {
        when(moduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> moduleServiceImpl.deleteModuleById(1L));
    }

    @Test
    void updateModules_ValidInput_ModulesUpdated() {
        List<UpdateModuleInCourseRequest> updateDTOs = List.of(new UpdateModuleInCourseRequest(), new UpdateModuleInCourseRequest());
        course.setModules(List.of(currentModulesMap.get(1L), currentModulesMap.get(2L)));
        when(moduleRepository.save(any(Module.class))).thenAnswer(i -> i.getArguments()[0]);

        List<Module> result = moduleServiceImpl.updateModules(updateDTOs, course);

        assertNotNull(result);
        assertEquals(updateDTOs.size(), result.size());
        verify(moduleRepository, times(1)).deleteAll(any());
    }

    @Test
    void updateModules_EmptyList_NoModulesUpdatedOrDeleted() {
        List<UpdateModuleInCourseRequest> emptyUpdateDTOs = new ArrayList<>();

        List<Module> result = moduleServiceImpl.updateModules(emptyUpdateDTOs, course);

        assertTrue(result.isEmpty());
        verify(moduleRepository, never()).deleteAll(anyList());
    }

    @Test
    void getAllModulesByCourse_ExistingCourseId_ReturnsSortedModules() {
        Long courseId = 1L;
        List<Module> modules = List.of(currentModulesMap.get(1L), currentModulesMap.get(2L));
        when(courseServiceImpl.fetchCourseById(courseId)).thenReturn(new Course());
        when(moduleRepository.findAllByCourseOrderByOrderNumAsc(any(Course.class))).thenReturn(modules);

        List<ModuleResponse> result = moduleServiceImpl.getAllModulesByCourse(courseId);

        assertEquals(modules.size(), result.size());
        assertTrue(result.get(0).getOrderNum() < result.get(1).getOrderNum());
    }

    @Test
    void getAllModulesByCourse_NonexistentCourseId_ThrowsResourceNotFoundException() {
        when(courseServiceImpl.fetchCourseById(anyLong())).thenThrow(new ResourceNotFoundException(Course.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> moduleServiceImpl.getAllModulesByCourse(1L));
    }

    @Test
    void createModules_ValidInput_ModulesCreated() {
        List<CreateModuleRequest> moduleDTOs = List.of(new CreateModuleRequest(), new CreateModuleRequest());
        when(moduleRepository.save(any(Module.class))).thenAnswer(i -> i.getArguments()[0]);

        moduleServiceImpl.createModules(moduleDTOs, course);

        verify(moduleRepository, times(moduleDTOs.size())).save(any(Module.class));
        verify(lessonServiceImpl, times(moduleDTOs.size())).createLessons(anyList(), any(Module.class));
    }

    @Test
    void createModules_EmptyList_NoModulesCreated() {
        List<CreateModuleRequest> emptyModuleDTOs = new ArrayList<>();

        moduleServiceImpl.createModules(emptyModuleDTOs, course);

        verify(moduleRepository, never()).save(any(Module.class));
        verify(lessonServiceImpl, never()).createLessons(anyList(), any(Module.class));
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void testUpdateModules(List<UpdateModuleInCourseRequest> moduleDTOS, List<Module> expectedModules) {
        // Arrange
        course.setModules(new ArrayList<>(currentModulesMap.values()));

        when(moduleRepository.save(any(Module.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(lessonServiceImpl.updateLessons(any(), any())).thenReturn(List.of());

        // Act
        List<Module> updatedModules = moduleServiceImpl.updateModules(moduleDTOS, course);

        // Assert
        assertEquals(expectedModules.size(), updatedModules.size());
        for (int i = 0; i < expectedModules.size(); i++) {
            assertEquals(expectedModules.get(i).getTitle(), updatedModules.get(i).getTitle());
            assertEquals(expectedModules.get(i).getOrderNum(), updatedModules.get(i).getOrderNum());
        }

        verify(moduleRepository, times(expectedModules.size())).save(any(Module.class));
        verify(lessonServiceImpl, times(moduleDTOS.size())).updateLessons(any(), any());
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                // 1: Add new modules
                Arguments.of(
                        List.of(
                                UpdateModuleInCourseRequest.builder().id(1L).title("Module 1").orderNum(1).build(),
                                UpdateModuleInCourseRequest.builder().id(2L).title("Module 2").orderNum(2).build(),
                                UpdateModuleInCourseRequest.builder().id(3L).title("Module 3").orderNum(3).build(),
                                UpdateModuleInCourseRequest.builder().title("New Module 1").orderNum(4).build(),
                                UpdateModuleInCourseRequest.builder().title("New Module 2").orderNum(5).build()
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
                                UpdateModuleInCourseRequest.builder().id(1L).title("Updated Module 1").orderNum(1).build(),
                                UpdateModuleInCourseRequest.builder().id(2L).title("Updated Module 2").orderNum(2).build(),
                                UpdateModuleInCourseRequest.builder().id(3L).title("Module 3").orderNum(3).build()
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
                                UpdateModuleInCourseRequest.builder().id(1L).title("Updated Module 1").orderNum(1).build(),
                                UpdateModuleInCourseRequest.builder().id(2L).title("Module 2").orderNum(2).build(),
                                UpdateModuleInCourseRequest.builder().id(3L).title("Module 3").orderNum(3).build(),
                                UpdateModuleInCourseRequest.builder().title("New Module 3").orderNum(4).build()
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
                                UpdateModuleInCourseRequest.builder().title("New Module 1").orderNum(4).build(),
                                UpdateModuleInCourseRequest.builder().title("New Module 2").orderNum(5).build()
                        ),
                        List.of(
                                Module.builder().id(4L).title("New Module 1").orderNum(4).build(),
                                Module.builder().id(5L).title("New Module 2").orderNum(5).build()
                        )
                ),
                // 5: Update existing modules and delete old
                Arguments.of(
                        List.of(
                                UpdateModuleInCourseRequest.builder().id(1L).title("Updated Module 1").orderNum(1).build(),
                                UpdateModuleInCourseRequest.builder().id(2L).title("Updated Module 2").orderNum(2).build()
                        ),
                        List.of(
                                Module.builder().id(1L).title("Updated Module 1").orderNum(1).build(),
                                Module.builder().id(2L).title("Updated Module 2").orderNum(2).build()
                        )
                ),
                // 6: Add new and update existing modules and delete old
                Arguments.of(
                        List.of(
                                UpdateModuleInCourseRequest.builder().id(1L).title("Updated Module 1").orderNum(1).build(),
                                UpdateModuleInCourseRequest.builder().title("New Module 3").orderNum(4).build()
                        ),
                        List.of(
                                Module.builder().id(1L).title("Updated Module 1").orderNum(1).build(),
                                Module.builder().id(4L).title("New Module 3").orderNum(4).build()
                        )
                )
        );
    }
}