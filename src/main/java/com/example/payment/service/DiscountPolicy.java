package com.example.payment.service;

import com.example.payment.domain.Member;
import com.example.payment.domain.PaymentMethod;
import com.example.payment.service.discount.DiscountResult;

public interface DiscountPolicy {
    
    DiscountResult discount(Member member, int price, PaymentMethod paymentMethod);

    // 정책의 종류를 구분
    DiscountType getDiscountType();
    
    enum DiscountType {
        BASE,       // 선적용 할인
        ADDITIONAL  // 추가 적용 할인
    }
}