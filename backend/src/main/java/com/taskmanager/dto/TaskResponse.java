package com.taskmanager.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private String priority;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long projectId;
    private String projectName;
    private Long assigneeId;
    private String assigneeUsername;
    private LocalDate dueDate;
    private long commentCount;
}
