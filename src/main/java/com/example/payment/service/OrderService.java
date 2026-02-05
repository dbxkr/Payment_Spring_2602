package com.example.payment.service;

import com.example.payment.domain.Member;
import com.example.payment.domain.Order;
import com.example.payment.repository.MemberRepository;
import com.example.payment.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor 
@Transactional(readOnly = true) // 기본적으로 읽기 전용 (성능 최적화)
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final DiscountPolicy discountPolicy; // ⭐ 핵심: 구체적인 정책(Fix/Rate)을 몰라도 됨!

    /**
     * 주문 생성 메서드
     */
    @Transactional // 데이터를 저장하니까 여기만 쓰기 허용
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        
        // 1. 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 2. 할인 정책에 "이 사람 얼마 깎아줘?" 물어봄 (위임)
        int discountPrice = discountPolicy.discount(member, itemPrice);

        // 3. 주문 생성 (할인 금액까지 포함해서)
        Order order = new Order(member, itemName, itemPrice, discountPrice);

        // 4. 저장 및 반환
        return orderRepository.save(order);
    }
}