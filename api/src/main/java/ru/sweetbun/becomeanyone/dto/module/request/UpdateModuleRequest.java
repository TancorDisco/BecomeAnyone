package ru.sweetbun.becomeanyone.dto.module.request;

import lombok.Builder;

@Builder
public record UpdateModuleRequest(
        String title,
        String description
) implements ModuleRequest {
}
