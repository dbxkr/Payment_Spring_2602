package com.example.payment.service.discount;

import com.example.payment.domain.DiscountRecord;
import com.example.payment.domain.Grade;
import com.example.payment.domain.Member;
import com.example.payment.domain.PaymentMethod;
import com.example.payment.service.DiscountPolicy;
import com.example.payment.service.DiscountRule;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

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
    public DiscountResult discount(Member member, int price, PaymentMethod method) {
        if (member.getGrade() == Grade.VIP) {
            // 최소금액 미달성 시: 할인 0원
            if (price < minOrderAmount) {
                return new DiscountResult(0, Collections.emptyList());
            }
            
            return new DiscountResult(//할인금액을 반환함
                discountFixAmount, 
                List.of(new DiscountRecord("VIP 고정 할인", discountFixAmount)) 
            );
        }
        
        // VIP아니면 할인 없음
        return new DiscountResult(0, Collections.emptyList());
    }

    @Override
    public DiscountType getDiscountType() {
        return DiscountType.BASE; // 기본 할인
    }
}