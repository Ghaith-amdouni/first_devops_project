package com.taskmanager.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private long totalProjects;
    private long totalTasks;
    private long completedTasks;
    private long inProgressTasks;
}
