package com.example.payment.service;

import com.example.payment.domain.Grade;
import com.example.payment.domain.Member;
import com.example.payment.domain.Order;
import com.example.payment.repository.MemberRepository;
import com.example.payment.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest // ⭐ 스프링 컨테이너(DB 포함)를 진짜로 띄움
@Transactional  // ⭐ 테스트 끝나면 DB 데이터 깔끔하게 롤백(삭제)해줌
class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired MemberRepository memberRepository;
    @Autowired OrderRepository orderRepository;

    @Test
    @DisplayName("주문이 정상 생성되고, 할인과 스냅샷이 DB에 잘 저장되어야 한다")
    void createOrder() {
        // 1. [Given] 회원 생성 및 DB 저장
        Member member = new Member("memberVVIP", Grade.VVIP);
        memberRepository.save(member); // 영속성 컨텍스트(DB)에 저장

        // 2. [When] 주문 서비스 호출 (20,000원짜리 주문)
        // 현재 @Primary가 Rate(10%)이므로 2000원 할인 예상
        Order order = orderService.createOrder(member.getId(), "RTX 5090", 20000);

        // 3. [Then] 검증
        
        // ID가 생성되었는가? (TSID 적용 확인)
        assertThat(order.getId()).isNotNull();
        
        // 할인 금액이 2000원(10%)이 맞는가?
        assertThat(order.getDiscountPrice()).isEqualTo(2000);
        
        // 최종 결제 금액이 18000원이 맞는가?
        assertThat(order.calculateFinalPrice()).isEqualTo(18000);

        // ★ 핵심: 스냅샷이 잘 박제되었는가?
        assertThat(order.getMemberGradeSnapshot()).isEqualTo(Grade.VVIP);
        
        // 눈으로 로그 확인해보기 (선택)
        System.out.println("주문 ID: " + order.getId());
        System.out.println("상품명: " + order.getItemName());
        System.out.println("할인액: " + order.getDiscountPrice());
        System.out.println("당시등급: " + order.getMemberGradeSnapshot());
    }
}