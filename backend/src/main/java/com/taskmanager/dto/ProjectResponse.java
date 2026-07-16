package com.taskmanager.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String ownerUsername;
    private List<UserSummaryResponse> members;
}
