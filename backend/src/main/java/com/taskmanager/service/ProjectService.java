package com.taskmanager.service;

import com.taskmanager.dto.ProjectMemberRequest;
import com.taskmanager.dto.ProjectRequest;
import com.taskmanager.dto.ProjectResponse;
import com.taskmanager.dto.UserSummaryResponse;
import com.taskmanager.entity.Project;
import com.taskmanager.entity.User;
import com.taskmanager.repository.ProjectRepository;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Transactional(readOnly = true)
    public List<ProjectResponse> getUserProjects(User user) {
        return projectRepository.findAccessibleByUserId(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(Long id, User user) {
        Project project = findProjectForUser(id, user);
        return toResponse(project);
    }

    public ProjectResponse createProject(ProjectRequest request, User user) {
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(user)
                .build();

        project = projectRepository.save(project);
        return toResponse(project);
    }

    public ProjectResponse updateProject(Long id, ProjectRequest request, User user) {
        Project project = findProjectForUser(id, user);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project = projectRepository.save(project);
        return toResponse(project);
    }

    public void deleteProject(Long id, User user) {
        Project project = findProjectForOwner(id, user);
        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getProjectMembers(Long id, User user) {
        Project project = findProjectForUser(id, user);
        return getAssignableUsers(project).stream()
                .map(this::toUserSummary)
                .toList();
    }

    public List<UserSummaryResponse> addMember(Long id, ProjectMemberRequest request, User user) {
        Project project = findProjectForOwner(id, user);
        User member = userRepository.findByUsername(request.getUsername())
                .or(() -> userRepository.findByEmail(request.getUsername()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (project.getOwner().getId().equals(member.getId())) {
            throw new RuntimeException("Project owner is already a member");
        }
        boolean alreadyMember = project.getMembers().stream()
                .anyMatch(existing -> existing.getId().equals(member.getId()));
        if (!alreadyMember) {
            project.getMembers().add(member);
            projectRepository.save(project);
        }
        return getAssignableUsers(project).stream().map(this::toUserSummary).toList();
    }

    public List<UserSummaryResponse> removeMember(Long id, Long memberId, User user) {
        Project project = findProjectForOwner(id, user);
        project.getMembers().removeIf(member -> member.getId().equals(memberId));
        taskRepository.findByProjectIdAndAssigneeId(project.getId(), memberId)
                .forEach(task -> task.setAssignee(null));
        projectRepository.save(project);
        return getAssignableUsers(project).stream().map(this::toUserSummary).toList();
    }

    public Project findProjectForUser(Long id, User user) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (!hasAccess(project, user)) {
            throw new RuntimeException("Access denied");
        }
        return project;
    }

    public List<User> getAssignableUsers(Project project) {
        return Stream.concat(Stream.of(project.getOwner()), project.getMembers().stream())
                .filter(Objects::nonNull)
                .filter(distinctById())
                .sorted(Comparator.comparing(User::getUsername))
                .toList();
    }

    public boolean hasAccess(Project project, User user) {
        return project.getOwner().getId().equals(user.getId())
                || project.getMembers().stream().anyMatch(member -> member.getId().equals(user.getId()));
    }

    public boolean canAssignTo(Project project, Long userId) {
        return getAssignableUsers(project).stream().anyMatch(member -> member.getId().equals(userId));
    }

    private Project findProjectForOwner(Long id, User user) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Project not found"));
        if (!project.getOwner().getId().equals(user.getId())) {
            throw new RuntimeException("Only the project owner can do this");
        }
        return project;
    }

    private ProjectResponse toResponse(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .createdAt(project.getCreatedAt())
                .ownerUsername(project.getOwner().getUsername())
                .members(getAssignableUsers(project).stream()
                        .map(this::toUserSummary)
                        .toList())
                .build();
    }

    private UserSummaryResponse toUserSummary(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    private java.util.function.Predicate<User> distinctById() {
        java.util.Set<Long> seen = new java.util.HashSet<>();
        return user -> seen.add(user.getId());
    }
}
