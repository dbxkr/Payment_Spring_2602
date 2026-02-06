package com.example.payment.service.discount;

import com.example.payment.domain.Grade;
import com.example.payment.domain.Member;
import com.example.payment.domain.PaymentMethod; // 추가됨
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class FixDiscountPolicyTest {

    FixDiscountPolicy discountPolicy = new FixDiscountPolicy();

    @Test
    @DisplayName("VIP는 계산된 최소 주문 기준액을 충족하면 할인이 적용된다")
    void vip_amount_cut_line_ok() {
        // given
        Member member = new Member("memberVIP", Grade.VIP);
        int minAmount = discountPolicy.getMinOrderAmount();

        // when: 파라미터 추가(PaymentMethod), 리턴 타입 변경(DiscountResult)
        DiscountResult result = discountPolicy.discount(member, minAmount, PaymentMethod.CREDIT_CARD);

        // then: 객체 안에서 금액 꺼내서 비교
        assertThat(result.getTotalDiscountAmount()).isGreaterThan(0);
    }

    @Test
    @DisplayName("VIP는 최소 주문 기준액에 미달하면 할인이 적용되지 않는다")
    void vip_amount_cut_line_fail() {
        // given
        Member member = new Member("memberVIP", Grade.VIP);
        int minAmount = discountPolicy.getMinOrderAmount();

        // when
        int price = minAmount - 10;
        DiscountResult result = discountPolicy.discount(member, price, PaymentMethod.CREDIT_CARD);

        // then
        assertThat(result.getTotalDiscountAmount()).isEqualTo(0);
    }
    
    @Test
    @DisplayName("일반 회원은 금액 상관없이 할인 없다")
    void basic_member_fail() {
        Member member = new Member("basic", Grade.NORMAL);
        
        DiscountResult result = discountPolicy.discount(member, 20000, PaymentMethod.CREDIT_CARD);
        
        assertThat(result.getTotalDiscountAmount()).isEqualTo(0);
    }
}