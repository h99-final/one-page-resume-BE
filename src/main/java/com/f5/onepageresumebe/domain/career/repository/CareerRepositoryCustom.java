package com.f5.onepageresumebe.domain.career.repository;


import com.f5.onepageresumebe.domain.career.entity.Career;

import java.util.Optional;

public interface CareerRepositoryCustom {

    Boolean existsByCareerIdAndUserEmail(Integer careerId, String userEmail);

    Optional<Career> findByCareerIdAndUserEmail(Integer careerId,String userEmail);
}
