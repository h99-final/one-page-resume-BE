package com.f5.onepageresumebe.domain.user.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Certification {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;

    @Column(unique = true,nullable = false, columnDefinition = "varchar(30)")
    private String email;

    @Column(unique = true,nullable = false, columnDefinition = "varchar(15)")
    private String code;

    @Builder(access = AccessLevel.PRIVATE)
    public Certification(String email, String code) {
        this.email = email;
        this.code = code;
    }

    public static Certification create(String email, String code) {
        Certification certification = Certification.builder()
                .email(email)
                .code(code)
                .build();

        return certification;
    }
    public void changeCode(String code) {
        this.code = code;
    }
}
