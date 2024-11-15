package ru.sweetbun.becomeanyone.domain.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.dto.profile.ProfileRequest;
import ru.sweetbun.becomeanyone.domain.entity.Profile;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.infrastructure.repository.ProfileRepository;

import java.util.List;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public Profile createProfile(ProfileRequest profileRequest) {
        Profile profile = modelMapper.map(profileRequest, Profile.class);
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
    public Profile updateProfile(ProfileRequest profileRequest, Profile profile) {
        if (profile == null) throw new ResourceNotFoundException("Profile not exist");
        modelMapper.map(profileRequest, profile);
        return profileRepository.save(profile);
    }

    @Transactional
    public long deleteProfileById(Long id) {
        getProfileById(id);
        profileRepository.deleteById(id);
        return id;
    }
}
