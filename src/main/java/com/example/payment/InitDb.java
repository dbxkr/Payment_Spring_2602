package com.example.payment;

import com.example.payment.domain.Grade;
import com.example.payment.domain.Member;
import com.example.payment.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class InitDb {

    private final MemberRepository memberRepository;

    @PostConstruct
    @Transactional
    public void init() {
        // 서버 켜질 때 테스트용 회원 생성
        if (memberRepository.count() == 0) {
            memberRepository.save(new Member("testVVIP", Grade.VVIP));
            memberRepository.save(new Member("testVIP", Grade.VIP));
            memberRepository.save(new Member("testNormal", Grade.NORMAL));
            System.out.println("====== [InitDb] 테스트용 회원 데이터 생성 완료 ======");
        }
    }
}