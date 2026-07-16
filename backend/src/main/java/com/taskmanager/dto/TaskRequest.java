package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskRequest {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private String priority;

    @NotNull
    private Long projectId;

    private Long assigneeId;

    private LocalDate dueDate;
}
