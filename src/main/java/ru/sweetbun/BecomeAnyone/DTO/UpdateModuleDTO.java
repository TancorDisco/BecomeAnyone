package ru.sweetbun.BecomeAnyone.DTO;

public record UpdateModuleDTO (
        String title,
        String description
) implements ModuleDTO{
}
