package com.f5.onepageresumebe.domain.mysql.repository;

import com.f5.onepageresumebe.domain.mysql.entity.UserStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserStackRepository extends JpaRepository<UserStack, Integer> {


    @Query("select us from UserStack us where us.user.id = :userId and us.stack.id = :stackId")
    Optional<UserStack> findByUserIdAndStackId(@Param("userId") Integer userId,
                                               @Param("stackId") Integer stackId);

//    @Query("select us.stack.name from UserStack us inner join us.stack where us.user.id = :userId")
//    List<String> findStackNamesByUserId(@Param("userId") Integer userId);

//    @Query("select distinct s.name from UserStack us inner join us.stack s " +
//            "inner join us.user u inner join u.portfolio p where p.id = :porfId")
//    List<String> findStackNamesByPorfId(@Param("porfId") Integer porfId);

    @Modifying
    @Query("delete from UserStack  us where us.user.id = :userId")
    void deleteAllUserStackByUserId(@Param("userId") Integer userId);
}
