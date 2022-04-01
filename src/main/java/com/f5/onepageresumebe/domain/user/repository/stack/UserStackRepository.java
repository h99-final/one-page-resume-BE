package com.f5.onepageresumebe.domain.user.repository.stack;

import com.f5.onepageresumebe.domain.user.entity.UserStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserStackRepository extends JpaRepository<UserStack, Integer>,UserStackRepositoryCustom {

    @Modifying
    @Query("delete from UserStack us where us.user.id = :userId")
    void deleteAllUserStackByUserId(@Param("userId") Integer userId);
}
