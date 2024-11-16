package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.dto.sociallink.SocialLinkRequest;
import ru.sweetbun.becomeanyone.entity.SocialLink;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.SocialLinkRepository;

import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class SocialLinkService {

    private final SocialLinkRepository socialLinkRepository;

    private final ModelMapper modelMapper;

    @Transactional
    public SocialLink createSocialLink(SocialLinkRequest socialLinkRequest) {
        SocialLink socialLink = modelMapper.map(socialLinkRequest, SocialLink.class);
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
    public SocialLink updateSocialLink(SocialLinkRequest socialLinkRequest, Long id) {
        SocialLink socialLink = getSocialLinkById(id);
        modelMapper.map(socialLinkRequest, socialLink);
        return socialLinkRepository.save(socialLink);
    }

    @Transactional
    public long deleteSocialLinkById(Long id) {
        getSocialLinkById(id);
        socialLinkRepository.deleteById(id);
        return id;
    }
}

