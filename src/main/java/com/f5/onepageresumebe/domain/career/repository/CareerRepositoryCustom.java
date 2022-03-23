package com.f5.onepageresumebe.domain.career.repository;

import com.f5.onepageresumebe.domain.career.entity.Career;

import java.util.Optional;

public interface CareerRepositoryCustom {

    Optional<Career> findByCareerIdAndUserEmail(Integer careerId, String userEmail);
}
