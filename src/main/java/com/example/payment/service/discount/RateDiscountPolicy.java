package com.example.payment.service.discount;

import com.example.payment.domain.Grade;
import com.example.payment.domain.Member;
import com.example.payment.service.DiscountPolicy;
import com.example.payment.service.DiscountRule;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
@Primary
public class RateDiscountPolicy implements DiscountPolicy {
    
    // N% 할인의 N
    private static final double DISCOUNT_PERCENT = DiscountRule.VVIP_DISCOUNT_RATE;

    @Override
    public int discount(Member member, int price) { //퍼센트 할인인만큼 소수점 오차가 나지 않게 금융표준인 BigDecimal과 버림 규칙을 이용
        if (member.getGrade() == Grade.VVIP) {
            BigDecimal originalPrice = new BigDecimal(price);
            BigDecimal percent = new BigDecimal(DISCOUNT_PERCENT);

            BigDecimal discountAmount = originalPrice.multiply(percent)
                    .divide(new BigDecimal(100));

            return discountAmount.setScale(0, RoundingMode.DOWN).intValue();
        }
        return 0;
    }
}