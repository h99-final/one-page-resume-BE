package com.f5.onepageresumebe.domain.career.repository;

import com.f5.onepageresumebe.domain.career.entity.Career;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CareerRepository extends JpaRepository<Career, Integer>, CareerRepositoryCustom {

    @Query("select c from Career c inner join fetch c.portfolio p where p.id = :porfId order by c.endTime desc")
    List<Career> findAllByPorfIdOrderByEndTimeDesc(@Param("porfId") Integer porfId);

    @Modifying
    @Query("delete from Career c where c.portfolio.id = :porfId")
    void deleteAllByPorfId(@Param("porfId") Integer porfId);

    @Modifying
    @Query("delete from Career c where c.id = :id")
    void deleteById(@Param("id") Integer id);

//    @Query("select c from Career c inner join fetch c.portfolio p inner join fetch p.user u" +
//            " where u.email = :userEmail and c.id = :careerId")
//    Optional<Career> findByIdAndUserEmail(@Param("careerId") Integer careerId,
//                                          @Param("userEmail") String userEmail);
}
