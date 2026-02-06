package com.example.payment.service.discount;

import com.example.payment.domain.DiscountRecord;
import com.example.payment.domain.Member;
import com.example.payment.domain.PaymentMethod;
import com.example.payment.service.DiscountPolicy;
import com.example.payment.service.DiscountRule;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;

@Component
public class PointDiscountPolicy implements DiscountPolicy {

    @Override
    public DiscountResult discount(Member member, int price, PaymentMethod method) {
        if (method == PaymentMethod.POINT) {
            BigDecimal originalPrice = new BigDecimal(price);
            BigDecimal percent = new BigDecimal(DiscountRule.POINT_DISCOUNT_RATE);

            // 추가 할인의 경우 올림처리
            int discountAmount = originalPrice.multiply(percent)
                    .divide(new BigDecimal(100))
                    .setScale(0, RoundingMode.CEILING)
                    .intValue();

            return new DiscountResult(
                discountAmount, 
                List.of(new DiscountRecord("포인트 결제 추가 할인(5%)", discountAmount))
            );
        }
        return new DiscountResult(0, Collections.emptyList());
    }

    @Override
    public DiscountType getDiscountType() {
        return DiscountType.ADDITIONAL;
    }
}