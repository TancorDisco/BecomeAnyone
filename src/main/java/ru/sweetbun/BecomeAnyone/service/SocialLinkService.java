package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sweetbun.BecomeAnyone.DTO.SocialLinkDTO;
import ru.sweetbun.BecomeAnyone.entity.SocialLink;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.SocialLinkRepository;

import java.util.List;

@Service
public class SocialLinkService {

    private final SocialLinkRepository socialLinkRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public SocialLinkService(SocialLinkRepository socialLinkRepository, ModelMapper modelMapper) {
        this.socialLinkRepository = socialLinkRepository;
        this.modelMapper = modelMapper;
    }

    public SocialLink createSocialLink(SocialLinkDTO socialLinkDTO) {
        SocialLink socialLink = modelMapper.map(socialLinkDTO, SocialLink.class);
        return socialLinkRepository.save(socialLink);
    }

    public SocialLink getSocialLinkById(Long id) {
        return socialLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SocialLink.class.getSimpleName(), id));
    }

    public List<SocialLink> getAllSocialLinks() {
        return socialLinkRepository.findAll();
    }

    public SocialLink updateSocialLink(SocialLink SocialLink, Long id) {
        SocialLink socialLink = getSocialLinkById(id);
        socialLink = modelMapper.map(socialLink, SocialLink.class);
        return socialLinkRepository.save(socialLink);
    }

    public void deleteSocialLinkById(Long id) {
        socialLinkRepository.deleteById(id);
    }
}

