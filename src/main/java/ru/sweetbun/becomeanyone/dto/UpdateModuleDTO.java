package ru.sweetbun.becomeanyone.dto;

import lombok.Builder;

@Builder
public record UpdateModuleDTO (
        String title,
        String description
) implements ModuleDTO{
}
