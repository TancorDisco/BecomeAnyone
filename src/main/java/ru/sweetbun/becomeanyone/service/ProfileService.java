package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.dto.ProfileDTO;
import ru.sweetbun.becomeanyone.entity.Profile;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.ProfileRepository;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Profile createProfile(ProfileDTO profileDTO) {
        Profile profile = modelMapper.map(profileDTO, Profile.class);
        return profileRepository.save(profile);
    }

    public Profile getProfileById(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Profile.class, id));
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }

    @Transactional
    public Profile updateProfile(ProfileDTO profileDTO, Profile profile) {
        if (profile == null) throw new ResourceNotFoundException("Profile not exist");
        modelMapper.map(profileDTO, profile);
        return profileRepository.save(profile);
    }

    @Transactional
    public long deleteProfileById(Long id) {
        getProfileById(id);
        profileRepository.deleteById(id);
        return id;
    }
}