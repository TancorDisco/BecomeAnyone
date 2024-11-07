package ru.sweetbun.BecomeAnyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.DTO.SocialLinkDTO;
import ru.sweetbun.BecomeAnyone.entity.SocialLink;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.SocialLinkRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SocialLinkService {

    private final SocialLinkRepository socialLinkRepository;

    private final ModelMapper modelMapper;

    @Transactional
    public SocialLink createSocialLink(SocialLinkDTO socialLinkDTO) {
        SocialLink socialLink = modelMapper.map(socialLinkDTO, SocialLink.class);
        return socialLinkRepository.save(socialLink);
    }

    public SocialLink getSocialLinkById(Long id) {
        return socialLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(SocialLink.class, id));
    }

    public List<SocialLink> getAllSocialLinks() {
        return socialLinkRepository.findAll();
    }

    @Transactional
    public SocialLink updateSocialLink(SocialLink SocialLink, Long id) {
        SocialLink socialLink = getSocialLinkById(id);
        socialLink = modelMapper.map(socialLink, SocialLink.class);
        return socialLinkRepository.save(socialLink);
    }

    @Transactional
    public long deleteSocialLinkById(Long id) {
        getSocialLinkById(id);
        socialLinkRepository.deleteById(id);
        return id;
    }
}

