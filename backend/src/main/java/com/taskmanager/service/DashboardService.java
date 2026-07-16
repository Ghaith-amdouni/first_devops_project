package com.taskmanager.service;

import com.taskmanager.dto.DashboardResponse;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public DashboardResponse getDashboard(User user) {
        long totalProjects = projectRepository.countAccessibleByUserId(user.getId());
        long totalTasks = taskRepository.countAccessibleByUserId(user.getId());
        long completedTasks = taskRepository.countAccessibleByUserIdAndStatus(user.getId(), Task.Status.DONE);
        long inProgressTasks = taskRepository.countAccessibleByUserIdAndStatus(user.getId(), Task.Status.IN_PROGRESS);

        return DashboardResponse.builder()
                .totalProjects(totalProjects)
                .totalTasks(totalTasks)
                .completedTasks(completedTasks)
                .inProgressTasks(inProgressTasks)
                .build();
    }
}
