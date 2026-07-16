package com.taskmanager.controller;

import com.taskmanager.dto.ProjectMemberRequest;
import com.taskmanager.dto.ProjectRequest;
import com.taskmanager.dto.ProjectResponse;
import com.taskmanager.dto.UserSummaryResponse;
import com.taskmanager.entity.User;
import com.taskmanager.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getProjects(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(projectService.getUserProjects(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProject(@PathVariable Long id,
                                                      @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(projectService.getProject(id, user));
    }

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request,
                                                         @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(projectService.createProject(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable Long id,
                                                         @Valid @RequestBody ProjectRequest request,
                                                         @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(projectService.updateProject(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id,
                                              @AuthenticationPrincipal User user) {
        projectService.deleteProject(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/members")
    public ResponseEntity<List<UserSummaryResponse>> getMembers(@PathVariable Long id,
                                                                @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(projectService.getProjectMembers(id, user));
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<List<UserSummaryResponse>> addMember(@PathVariable Long id,
                                                               @Valid @RequestBody ProjectMemberRequest request,
                                                               @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(projectService.addMember(id, request, user));
    }

    @DeleteMapping("/{id}/members/{memberId}")
    public ResponseEntity<List<UserSummaryResponse>> removeMember(@PathVariable Long id,
                                                                  @PathVariable Long memberId,
                                                                  @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(projectService.removeMember(id, memberId, user));
    }
}
