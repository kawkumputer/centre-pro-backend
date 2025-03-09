package com.kawkumputer.centreeducatif.service;

import com.kawkumputer.centreeducatif.domain.Project;
import com.kawkumputer.centreeducatif.domain.User;
import com.kawkumputer.centreeducatif.dto.ProjectRequest;
import com.kawkumputer.centreeducatif.dto.ProjectResponse;
import com.kawkumputer.centreeducatif.dto.UserResponse;
import com.kawkumputer.centreeducatif.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserService userService;

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        log.debug("Creating new project with name: {}", request.getName());
        
        User currentUser = userService.getCurrentUser()
            .orElseThrow(() -> new RuntimeException("Aucun utilisateur connecté"));

        if (projectRepository.existsByNameAndOwner(request.getName(), currentUser)) {
            throw new RuntimeException("Vous avez déjà un projet avec ce nom");
        }

        Project project = Project.builder()
            .name(request.getName())
            .description(request.getDescription())
            .startDate(request.getStartDate())
            .expectedEndDate(request.getExpectedEndDate())
            .status(request.getStatus())
            .initialBudget(request.getInitialBudget())
            .owner(currentUser)
            .build();

        Project savedProject = projectRepository.save(project);
        log.info("Project created successfully with id: {}", savedProject.getId());
        
        return toProjectResponse(savedProject);
    }

    @Transactional(readOnly = true)
    public ProjectResponse getProject(Long id) {
        log.debug("Fetching project with id: {}", id);
        
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        // Vérifier que l'utilisateur a accès au projet
        User currentUser = userService.getCurrentUser()
            .orElseThrow(() -> new RuntimeException("Aucun utilisateur connecté"));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Vous n'avez pas accès à ce projet");
        }

        return toProjectResponse(project);
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> getProjects(Pageable pageable) {
        log.debug("Fetching projects page: {}", pageable);
        
        User currentUser = userService.getCurrentUser()
            .orElseThrow(() -> new RuntimeException("Aucun utilisateur connecté"));

        return projectRepository.findByOwner(currentUser, pageable)
            .map(this::toProjectResponse);
    }

    @Transactional
    public ProjectResponse updateProject(Long id, ProjectRequest request) {
        log.debug("Updating project with id: {}", id);
        
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        // Vérifier que l'utilisateur a accès au projet
        User currentUser = userService.getCurrentUser()
            .orElseThrow(() -> new RuntimeException("Aucun utilisateur connecté"));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Vous n'avez pas accès à ce projet");
        }

        // Vérifier si le nouveau nom n'est pas déjà utilisé par un autre projet
        if (!project.getName().equals(request.getName()) && 
            projectRepository.existsByNameAndOwner(request.getName(), currentUser)) {
            throw new RuntimeException("Vous avez déjà un projet avec ce nom");
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setStartDate(request.getStartDate());
        project.setExpectedEndDate(request.getExpectedEndDate());
        project.setStatus(request.getStatus());
        project.setInitialBudget(request.getInitialBudget());

        Project updatedProject = projectRepository.save(project);
        log.info("Project updated successfully with id: {}", updatedProject.getId());
        
        return toProjectResponse(updatedProject);
    }

    @Transactional
    public void deleteProject(Long id) {
        log.debug("Deleting project with id: {}", id);
        
        Project project = projectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Projet non trouvé"));

        // Vérifier que l'utilisateur a accès au projet
        User currentUser = userService.getCurrentUser()
            .orElseThrow(() -> new RuntimeException("Aucun utilisateur connecté"));

        if (!project.getOwner().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Vous n'avez pas accès à ce projet");
        }

        projectRepository.delete(project);
        log.info("Project deleted successfully with id: {}", id);
    }

    private ProjectResponse toProjectResponse(Project project) {
        return ProjectResponse.builder()
            .id(project.getId())
            .name(project.getName())
            .description(project.getDescription())
            .startDate(project.getStartDate())
            .expectedEndDate(project.getExpectedEndDate())
            .status(project.getStatus())
            .statusDisplayName(project.getStatus().getDisplayName())
            .initialBudget(project.getInitialBudget())
            .owner(UserResponse.builder()
                .id(project.getOwner().getId())
                .firstName(project.getOwner().getFirstName())
                .lastName(project.getOwner().getLastName())
                .email(project.getOwner().getEmail())
                .role(project.getOwner().getRole())
                .build())
            .createdAt(project.getCreatedAt())
            .updatedAt(project.getUpdatedAt())
            .build();
    }
}
