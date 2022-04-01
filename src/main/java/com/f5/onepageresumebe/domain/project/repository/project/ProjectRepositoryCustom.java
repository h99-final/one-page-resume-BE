package com.f5.onepageresumebe.domain.project.repository.project;

import com.f5.onepageresumebe.domain.project.entity.Project;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProjectRepositoryCustom {

    List<Project> findAllByUserEmail(String userEmail);

    Optional<Project> findByUserEmailAndProjectId(String userEmail, Integer projectId);

    List<Project> findAllByStackNames(List<String> stackNames,Pageable pageable);
}
