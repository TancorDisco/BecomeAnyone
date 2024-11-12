package ru.sweetbun.become_anyone.DTO;

import lombok.Builder;

@Builder
public record UpdateModuleDTO (
        String title,
        String description
) implements ModuleDTO{
}
