package com.taskmanager.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCommentResponse {

    private Long id;
    private String content;
    private LocalDateTime createdAt;
    private Long authorId;
    private String authorUsername;
}
