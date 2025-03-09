package com.kawkumputer.centreeducatif.repository;

import com.kawkumputer.centreeducatif.domain.Project;
import com.kawkumputer.centreeducatif.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findByOwner(User owner, Pageable pageable);
    List<Project> findByOwner(User owner);
    boolean existsByNameAndOwner(String name, User owner);
}
