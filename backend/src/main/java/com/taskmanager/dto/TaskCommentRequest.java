package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCommentRequest {

    @NotBlank
    private String content;
}
