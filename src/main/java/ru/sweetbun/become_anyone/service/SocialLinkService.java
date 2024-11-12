package ru.sweetbun.become_anyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.become_anyone.DTO.SocialLinkDTO;
import ru.sweetbun.become_anyone.entity.SocialLink;
import ru.sweetbun.become_anyone.exception.ResourceNotFoundException;
import ru.sweetbun.become_anyone.repository.SocialLinkRepository;

import java.util.List;

@Transactional(readOnly = true)
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
    public SocialLink updateSocialLink(SocialLinkDTO socialLinkDTO, Long id) {
        SocialLink socialLink = getSocialLinkById(id);
        modelMapper.map(socialLinkDTO, socialLink);
        return socialLinkRepository.save(socialLink);
    }

    @Transactional
    public long deleteSocialLinkById(Long id) {
        getSocialLinkById(id);
        socialLinkRepository.deleteById(id);
        return id;
    }
}

