package com.f5.onepageresumebe.domain.common;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;


@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public abstract class TimeEntity {

    @Column(name="created_at",columnDefinition = "timestamp", updatable = false)
    @CreatedDate
    LocalDateTime createdAt;

    @Column(name="updated_at",columnDefinition = "timestamp")
    @LastModifiedDate
    LocalDateTime updatedAt;
}

