package com.f5.onepageresumebe.domain.repository;

import com.f5.onepageresumebe.domain.entity.Stack;
import com.f5.onepageresumebe.domain.entity.User;
import com.f5.onepageresumebe.domain.entity.UserStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserStackRepository extends JpaRepository<UserStack, Integer> {


    @Query("select us from UserStack us where us.user.id = :userId and us.stack.id = :stackId")
    Optional<UserStack> findByUserIdAndStackId(@Param("userId") Integer userId,
                                               @Param("stackId") Integer stackId);

    @Query("select us.stack.name from UserStack us join us.stack where us.user.id = :userId")
    List<String> findStackNamesByUserId(@Param("userId") Integer userId);

    Optional<UserStack> findFirstByUserAndStack(User user, Stack stack);
}
