package com.taskmanager.repository;

import com.taskmanager.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByProjectId(Long projectId);
    List<Task> findByProjectIdAndStatus(Long projectId, Task.Status status);
    List<Task> findByProjectIdAndAssigneeId(Long projectId, Long assigneeId);
    long countByProjectId(Long projectId);
    long countByProjectIdAndStatus(Long projectId, Task.Status status);

    long countByProjectOwnerId(Long ownerId);
    long countByProjectOwnerIdAndStatus(Long ownerId, Task.Status status);

    @Query("""
            select count(distinct t)
            from Task t
            left join t.project.members m
            where t.project.owner.id = :userId or m.id = :userId
            """)
    long countAccessibleByUserId(@Param("userId") Long userId);

    @Query("""
            select count(distinct t)
            from Task t
            left join t.project.members m
            where (t.project.owner.id = :userId or m.id = :userId)
              and t.status = :status
            """)
    long countAccessibleByUserIdAndStatus(@Param("userId") Long userId,
                                          @Param("status") Task.Status status);
}
