package ru.sweetbun.BecomeAnyone.service;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sweetbun.BecomeAnyone.DTO.ProfileDTO;
import ru.sweetbun.BecomeAnyone.entity.Profile;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.ProfileRepository;

import java.util.List;

@Slf4j
@Service
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, ModelMapper modelMapper) {
        this.profileRepository = profileRepository;
        this.modelMapper = modelMapper;
    }

    public Profile createProfile(ProfileDTO profileDTO) {
        Profile profile = modelMapper.map(profileDTO, Profile.class);
        return profileRepository.save(profile);
    }

    public Profile getProfileById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Profile.class.getSimpleName(), id));
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    public Profile updateProfile(ProfileDTO profileDTO, Long id) {
        Profile profile = getProfileById(id);
        modelMapper.map(profileDTO, profile);
        return profileRepository.save(profile);
    }

    public void deleteProfileById(Long id) {
        profileRepository.deleteById(id);
    }
}
