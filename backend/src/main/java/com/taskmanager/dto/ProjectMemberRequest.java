package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectMemberRequest {

    @NotBlank
    private String username;
}
