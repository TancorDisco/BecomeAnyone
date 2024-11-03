package ru.sweetbun.BecomeAnyone.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateModuleDTO implements ModuleDTO{

    private String title;
    private String description;
}
