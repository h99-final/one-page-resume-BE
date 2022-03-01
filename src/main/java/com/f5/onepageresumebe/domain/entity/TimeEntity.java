package com.f5.onepageresumebe.domain.entity;

@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class TimeEntity {

    @Column(name="created_at",columnDefinition = "timestamp", updatable = false)
    @CreatedDate
    LocalDateTime createdAt;

    @Column(name="updated_at",columnDefinition = "timestamp")
    @LastModifiedDate
    LocalDateTime updatedAt;

}
