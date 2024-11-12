package ru.sweetbun.BecomeAnyone.DTO;

import lombok.Builder;

@Builder
public record UpdateModuleDTO (
        String title,
        String description
) implements ModuleDTO{
}
