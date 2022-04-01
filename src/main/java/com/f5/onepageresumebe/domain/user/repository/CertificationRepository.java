package com.f5.onepageresumebe.domain.user.repository;

import com.f5.onepageresumebe.domain.user.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertificationRepository extends JpaRepository<Certification, Integer> {
    Certification findCertificationByEmailAndCode(String email, String code);
    Certification findCertificationByEmail(String email);
}
