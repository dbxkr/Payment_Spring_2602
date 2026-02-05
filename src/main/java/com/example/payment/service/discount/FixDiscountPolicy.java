package com.example.payment.service.discount;

import com.example.payment.domain.Grade;
import com.example.payment.domain.Member;
import com.example.payment.service.DiscountPolicy;
import com.example.payment.service.DiscountRule;

import lombok.Getter;

import org.springframework.stereotype.Component;

@Component
@Getter
public class FixDiscountPolicy implements DiscountPolicy {

    private final int discountFixAmount;
    private final int minOrderAmount;

    public FixDiscountPolicy() {
        this.discountFixAmount = DiscountRule.VIP_DISCOUNT_AMOUNT;
        
        // VIP등급이 VVIP의 할인율보다 좋아지는 경우를 방지하는 할인율 역산
        double rawThreshold = discountFixAmount / (DiscountRule.VVIP_DISCOUNT_RATE / 100.0);
        
        // 1000원 단위로 올림 처리
        this.minOrderAmount = (int) (Math.ceil(rawThreshold / 1000.0) * 1000.0);
    }

    @Override
    public int discount(Member member, int price) {// 최소금액 미달성시 할인 적용 X
        if (member.getGrade() == Grade.VIP) {
            if (price < minOrderAmount) {
                return 0;
            }
            return discountFixAmount;
        }
        return 0;
    }
}