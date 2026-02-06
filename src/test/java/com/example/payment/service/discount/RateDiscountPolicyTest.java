package com.example.payment.service.discount;

import com.example.payment.domain.Grade;
import com.example.payment.domain.Member;
import com.example.payment.domain.PaymentMethod; // 추가됨
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RateDiscountPolicyTest {

    RateDiscountPolicy discountPolicy = new RateDiscountPolicy();

    @Test
    @DisplayName("VVIP는 10% 할인이 적용되어야 한다")
    void vvip_o() {
        // given
        Member member = new Member("memberVVIP", Grade.VVIP);
        
        // when
        // PaymentMethod 추가, 리턴 타입 변경
        DiscountResult result = discountPolicy.discount(member, 20000, PaymentMethod.CREDIT_CARD);
        
        // then
        // 20000 * 10% = 2000
        assertThat(result.getTotalDiscountAmount()).isEqualTo(2000);
    }

    @Test
    @DisplayName("VVIP가 아니면 할인이 적용되지 않아야 한다")
    void vvip_x() {
        // given
        Member member = new Member("memberBASIC", Grade.NORMAL);
        
        // when
        DiscountResult result = discountPolicy.discount(member, 20000, PaymentMethod.CREDIT_CARD);
        
        // then
        assertThat(result.getTotalDiscountAmount()).isEqualTo(0);
    }
}