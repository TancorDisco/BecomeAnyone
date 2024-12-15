package ru.sweetbun.becomeanyone.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUserPrincipal{
    private final Long id;
    private final String username;
}
