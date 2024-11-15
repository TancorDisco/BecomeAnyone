package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.sweetbun.becomeanyone.dto.SocialLinkDTO;
import ru.sweetbun.becomeanyone.domain.service.SocialLinkService;
import ru.sweetbun.becomeanyone.infrastructure.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.domain.entity.SocialLink;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.infrastructure.repository.SocialLinkRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialLinkServiceTests {

    @Mock
    private SocialLinkRepository socialLinkRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @InjectMocks
    private SocialLinkService socialLinkService;

    private SocialLink socialLink;

    @BeforeEach
    void setUp() {
        socialLinkService = new SocialLinkService(socialLinkRepository, modelMapper);
        socialLink = new SocialLink();
    }

    @Test
    void createSocialLink_ValidDTO_Success() {
        SocialLinkDTO dto = new SocialLinkDTO("VK", "...");
        when(socialLinkRepository.save(any(SocialLink.class))).thenReturn(socialLink);

        SocialLink result = socialLinkService.createSocialLink(dto);

        assertNotNull(result);
        verify(socialLinkRepository, times(1)).save(any(SocialLink.class));
    }

    @Test
    void getSocialLinkById_ExistingId_ReturnsSocialLink() {
        Long id = 1L;
        when(socialLinkRepository.findById(id)).thenReturn(Optional.of(socialLink));

        SocialLink result = socialLinkService.getSocialLinkById(id);

        assertNotNull(result);
        assertEquals(socialLink, result);
        verify(socialLinkRepository, times(1)).findById(id);
    }

    @Test
    void getSocialLinkById_NonExistingId_ThrowsResourceNotFoundException() {
        Long id = 1L;
        when(socialLinkRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> socialLinkService.getSocialLinkById(id));
        verify(socialLinkRepository, times(1)).findById(id);
    }

    @Test
    void getAllSocialLinks_NoConditions_ReturnsListOfSocialLinks() {
        List<SocialLink> socialLinks = List.of(socialLink, socialLink);

        when(socialLinkRepository.findAll()).thenReturn(socialLinks);

        List<SocialLink> result = socialLinkService.getAllSocialLinks();

        assertEquals(2, result.size());
        verify(socialLinkRepository, times(1)).findAll();
    }

    @Test
    void updateSocialLink_ExistingId_ReturnsUpdatedSocialLink() {
        Long id = 1L;
        SocialLinkDTO dto = new SocialLinkDTO("VK", "...");
        SocialLink existingLink = socialLink;
        SocialLink updatedLink = socialLink;

        when(socialLinkRepository.findById(id)).thenReturn(Optional.of(existingLink));
        when(socialLinkRepository.save(updatedLink)).thenReturn(updatedLink);

        SocialLink result = socialLinkService.updateSocialLink(dto, id);

        assertNotNull(result);
        assertEquals(updatedLink, result);
        verify(socialLinkRepository, times(1)).findById(id);
        verify(socialLinkRepository, times(1)).save(updatedLink);
    }

    @Test
    void updateSocialLink_NonExistingId_ThrowsResourceNotFoundException() {
        Long id = 1L;
        SocialLinkDTO dto = new SocialLinkDTO("VK", "...");

        when(socialLinkRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> socialLinkService.updateSocialLink(dto, id));
        verify(socialLinkRepository, times(1)).findById(id);
    }

    @Test
    void deleteSocialLinkById_ExistingId_ReturnsId() {
        Long id = 1L;
        when(socialLinkRepository.findById(id)).thenReturn(Optional.of(new SocialLink()));

        long result = socialLinkService.deleteSocialLinkById(id);

        assertEquals(id, result);
        verify(socialLinkRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteSocialLinkById_NonExistingId_ThrowsResourceNotFoundException() {
        Long id = 1L;
        when(socialLinkRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> socialLinkService.deleteSocialLinkById(id));
        verify(socialLinkRepository, never()).deleteById(id);
    }
}