package com.taskmanager.service;

import com.taskmanager.dto.StatusUpdateRequest;
import com.taskmanager.dto.TaskCommentRequest;
import com.taskmanager.dto.TaskCommentResponse;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskComment;
import com.taskmanager.entity.User;
import com.taskmanager.repository.TaskCommentRepository;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskCommentRepository taskCommentRepository;
    private final UserRepository userRepository;
    private final ProjectService projectService;

    @Transactional(readOnly = true)
    public List<TaskResponse> getTasks(Long projectId, String status, User user) {
        Task.Status taskStatus = parseStatus(status);

        if (projectId == null) {
            List<Task> tasks = taskStatus != null
                    ? taskRepository.findAccessibleByUserIdAndStatus(user.getId(), taskStatus)
                    : taskRepository.findAccessibleByUserId(user.getId());
            return tasks.stream().map(this::toResponse).toList();
        }

        projectService.findProjectForUser(projectId, user);
        List<Task> tasks;

        if (taskStatus != null) {
            tasks = taskRepository.findByProjectIdAndStatus(projectId, taskStatus);
        } else {
            tasks = taskRepository.findByProjectId(projectId);
        }

        return tasks.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse getTask(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        validateOwnership(task.getProject(), user);
        return toResponse(task);
    }

    public TaskResponse createTask(TaskRequest request, User user) {
        Project project = projectService.findProjectForUser(request.getProjectId(), user);

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(Task.Priority.valueOf(request.getPriority().toUpperCase()))
                .status(Task.Status.TODO)
                .dueDate(request.getDueDate())
                .assignee(resolveAssignee(project, request.getAssigneeId()))
                .project(project)
                .build();

        task = taskRepository.save(task);
        return toResponse(task);
    }

    public TaskResponse updateTask(Long id, TaskRequest request, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        validateOwnership(task.getProject(), user);
        if (request.getProjectId() != null && !task.getProject().getId().equals(request.getProjectId())) {
            throw new RuntimeException("Moving tasks between projects is not supported");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setPriority(Task.Priority.valueOf(request.getPriority().toUpperCase()));
        task.setDueDate(request.getDueDate());
        task.setAssignee(resolveAssignee(task.getProject(), request.getAssigneeId()));

        task = taskRepository.save(task);
        return toResponse(task);
    }

    public void deleteTask(Long id, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        validateOwnership(task.getProject(), user);
        taskRepository.delete(task);
    }

    public TaskResponse updateStatus(Long id, StatusUpdateRequest request, User user) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        validateOwnership(task.getProject(), user);

        task.setStatus(Task.Status.valueOf(request.getStatus().toUpperCase()));
        task = taskRepository.save(task);
        return toResponse(task);
    }

    @Transactional(readOnly = true)
    public List<TaskCommentResponse> getComments(Long taskId, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        validateOwnership(task.getProject(), user);
        return taskCommentRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(this::toCommentResponse)
                .toList();
    }

    public TaskCommentResponse addComment(Long taskId, TaskCommentRequest request, User user) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        validateOwnership(task.getProject(), user);

        TaskComment comment = TaskComment.builder()
                .task(task)
                .author(user)
                .content(request.getContent().trim())
                .build();

        return toCommentResponse(taskCommentRepository.save(comment));
    }

    private void validateOwnership(Project project, User user) {
        if (!projectService.hasAccess(project, user)) {
            throw new RuntimeException("Access denied");
        }
    }

    private User resolveAssignee(Project project, Long assigneeId) {
        if (assigneeId == null) {
            return null;
        }
        if (!projectService.canAssignTo(project, assigneeId)) {
            throw new RuntimeException("Assignee must be a project member");
        }
        return userRepository.findById(assigneeId)
                .orElseThrow(() -> new RuntimeException("Assignee not found"));
    }

    private Task.Status parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        return Task.Status.valueOf(status.toUpperCase());
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .priority(task.getPriority().name())
                .status(task.getStatus().name())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .projectId(task.getProject().getId())
                .projectName(task.getProject().getName())
                .assigneeId(task.getAssignee() != null ? task.getAssignee().getId() : null)
                .assigneeUsername(task.getAssignee() != null ? task.getAssignee().getUsername() : null)
                .dueDate(task.getDueDate())
                .commentCount(taskCommentRepository.countByTaskId(task.getId()))
                .build();
    }

    private TaskCommentResponse toCommentResponse(TaskComment comment) {
        return TaskCommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .authorId(comment.getAuthor().getId())
                .authorUsername(comment.getAuthor().getUsername())
                .build();
    }
}
