package com.f5.onepageresumebe.domain.mysql.repository;

import com.f5.onepageresumebe.domain.mysql.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification, Integer> {
    Certification findCertificationByEmailAndCode(String email, String code);
}
