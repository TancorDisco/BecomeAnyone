package ru.sweetbun.becomeanyone.dto.user;

import lombok.*;
import ru.sweetbun.becomeanyone.dto.enrollment.EnrollmentResponse;
import ru.sweetbun.becomeanyone.dto.profile.ProfileResponse;
import ru.sweetbun.becomeanyone.dto.role.RoleResponse;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Set<RoleResponse> roles;
    private ProfileResponse profile;
    private List<EnrollmentResponse> enrollments;
}