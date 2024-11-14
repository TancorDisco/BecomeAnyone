package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.sweetbun.becomeanyone.api.dto.ProfileDTO;
import ru.sweetbun.becomeanyone.domain.service.ProfileService;
import ru.sweetbun.becomeanyone.infrastructure.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.domain.entity.Profile;
import ru.sweetbun.becomeanyone.infrastructure.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.infrastructure.repository.ProfileRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTests {

    @Mock
    private ProfileRepository profileRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @InjectMocks
    private ProfileService profileService;

    private Profile profile;

    @BeforeEach
    void setUp() {
        profileService = new ProfileService(profileRepository, modelMapper);

        profile = new Profile();
    }

    @Test
    void createProfile_ValidProfileDTO_ProfileCreated() {
        ProfileDTO profileDTO = ProfileDTO.builder().build();
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        Profile createdProfile = profileService.createProfile(profileDTO);

        assertNotNull(createdProfile);
        verify(profileRepository, times(1)).save(any(Profile.class));
    }

    @Test
    void getProfileById_ExistingId_ProfileReturned() {
        Long id = 1L;
        when(profileRepository.findById(id)).thenReturn(Optional.of(profile));

        Profile foundProfile = profileService.getProfileById(id);

        assertNotNull(foundProfile);
        assertEquals(profile, foundProfile);
    }

    @Test
    void getProfileById_NonExistingId_ThrowsResourceNotFoundException() {
        Long id = 1L;
        when(profileRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> profileService.getProfileById(id));
    }

    @Test
    void getAllProfiles_HasProfiles_ListOfProfilesReturned() {
        List<Profile> profiles = List.of(profile, profile);
        when(profileRepository.findAll()).thenReturn(profiles);

        List<Profile> allProfiles = profileService.getAllProfiles();

        assertEquals(2, allProfiles.size());
    }

    @Test
    void getAllProfiles_NoProfiles_EmptyListReturned() {
        when(profileRepository.findAll()).thenReturn(List.of());

        List<Profile> allProfiles = profileService.getAllProfiles();

        assertTrue(allProfiles.isEmpty());
    }

    @Test
    void updateProfile_ExistingId_ProfileUpdated() {
        ProfileDTO profileDTO = ProfileDTO.builder().build();

        when(profileRepository.save(profile)).thenReturn(profile);

        Profile updatedProfile = profileService.updateProfile(profileDTO, profile);

        assertNotNull(updatedProfile);
        verify(profileRepository, times(1)).save(profile);
    }

    @Test
    void updateProfile_ProfileNonExist_ThrowsResourceNotFoundException() {
        ProfileDTO profileDTO = ProfileDTO.builder().build();

        assertThrows(ResourceNotFoundException.class, () -> profileService.updateProfile(profileDTO, null));
    }

    @Test
    void deleteProfileById_ExistingId_ProfileDeleted() {
        Long id = 1L;
        when(profileRepository.findById(id)).thenReturn(Optional.of(profile));

        long deletedId = profileService.deleteProfileById(id);

        assertEquals(id, deletedId);
        verify(profileRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteProfileById_NonExistingId_ThrowsResourceNotFoundException() {
        Long id = 1L;
        when(profileRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> profileService.deleteProfileById(id));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -1L, 100L})
    void getProfileById_InvalidId_ThrowsResourceNotFoundException(Long invalidId) {
        when(profileRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> profileService.getProfileById(invalidId));
    }
}