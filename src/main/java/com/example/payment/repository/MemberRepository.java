package com.example.payment.repository;
import com.example.payment.domain.Member;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    List<Member> findByNameStartingWith(String prefix);
    //테스트용 InitDb가 생성한 회원3명 검색
    List<Member> findTop3ByOrderByIdAsc();
}