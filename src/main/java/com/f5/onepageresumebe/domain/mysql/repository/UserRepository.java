package com.f5.onepageresumebe.domain.mysql.repository;

import com.f5.onepageresumebe.domain.mysql.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {

//    @Query("select u from User u left join fetch u.portfolio where u.email =:email")
//    Optional<User> findByEmail(@Param("email") String email);
    User findUserByNameAndPhoneNum(String name, String phoneNum);

}