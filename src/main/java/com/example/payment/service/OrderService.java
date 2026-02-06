package com.example.payment.service;

import com.example.payment.domain.Member;
import com.example.payment.domain.Order;
import com.example.payment.domain.PaymentMethod;
import com.example.payment.repository.MemberRepository;
import com.example.payment.repository.OrderRepository;
import com.example.payment.service.discount.DiscountResult;

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

    @Transactional
    public Order createOrder(Long memberId, String itemName, int itemPrice, PaymentMethod method) {
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 1. 할인 정책 실행 
        DiscountResult discountResult = discountPolicy.discount(member, itemPrice, method);

        // 2. 주문 생성
        Order order = new Order(
                member, 
                itemName, 
                itemPrice, 
                discountResult.getTotalDiscountAmount(), // 총 할인액
                discountResult.getHistory()              // 상세 내역
        );

        return orderRepository.save(order);
    }
}