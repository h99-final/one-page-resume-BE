package com.f5.onepageresumebe.repository;

import com.f5.onepageresumebe.domain.entity.Career;
import com.f5.onepageresumebe.domain.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerRepository extends JpaRepository<Career, Integer> {
}
