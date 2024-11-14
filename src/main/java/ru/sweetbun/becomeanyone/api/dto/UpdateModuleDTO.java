package ru.sweetbun.becomeanyone.api.dto;

import lombok.Builder;

@Builder
public record UpdateModuleDTO (
        String title,
        String description
) implements ModuleDTO{
}
