package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.Project;
import com.f5.onepageresumebe.domain.entity.ProjectImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectImgRepository extends JpaRepository<ProjectImg, Integer>  {
}
