package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectRequest {

    @NotBlank
    private String name;

    private String description;
}
