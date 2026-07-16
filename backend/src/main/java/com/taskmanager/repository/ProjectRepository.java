package com.taskmanager.repository;

import com.taskmanager.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    List<Project> findByOwnerId(Long ownerId);
    long countByOwnerId(Long ownerId);

    @Query("""
            select distinct p
            from Project p
            left join p.members m
            where p.owner.id = :userId or m.id = :userId
            order by p.createdAt desc
            """)
    List<Project> findAccessibleByUserId(@Param("userId") Long userId);

    @Query("""
            select count(distinct p)
            from Project p
            left join p.members m
            where p.owner.id = :userId or m.id = :userId
            """)
    long countAccessibleByUserId(@Param("userId") Long userId);
}
