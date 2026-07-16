package com.taskmanager.controller;

import com.taskmanager.dto.StatusUpdateRequest;
import com.taskmanager.dto.TaskCommentRequest;
import com.taskmanager.dto.TaskCommentResponse;
import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.entity.User;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public ResponseEntity<List<TaskResponse>> getTasks(@RequestParam(required = false) Long projectId,
                                                       @RequestParam(required = false) String status,
                                                       @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.getTasks(projectId, status, user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable Long id,
                                                @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.getTask(id, user));
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request,
                                                   @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.createTask(request, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable Long id,
                                                   @Valid @RequestBody TaskRequest request,
                                                   @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.updateTask(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id,
                                           @AuthenticationPrincipal User user) {
        taskService.deleteTask(id, user);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(@PathVariable Long id,
                                                     @Valid @RequestBody StatusUpdateRequest request,
                                                     @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.updateStatus(id, request, user));
    }

    @GetMapping("/{id}/comments")
    public ResponseEntity<List<TaskCommentResponse>> getComments(@PathVariable Long id,
                                                                 @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.getComments(id, user));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<TaskCommentResponse> addComment(@PathVariable Long id,
                                                          @Valid @RequestBody TaskCommentRequest request,
                                                          @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(taskService.addComment(id, request, user));
    }
}
