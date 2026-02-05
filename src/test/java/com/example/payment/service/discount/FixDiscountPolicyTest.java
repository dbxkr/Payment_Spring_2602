package com.example.payment.service.discount;

import com.example.payment.domain.Grade;
import com.example.payment.domain.Member;
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
        
        // 최소할인금액을 불러옴
        int minAmount = discountPolicy.getMinOrderAmount(); 

        // when: 딱 그 기준 금액만큼 주문했을 때
        int discount = discountPolicy.discount(member, minAmount);

        // then: 할인이 적용되어야 함
        // (할인 금액도 하드코딩 대신 객체에서 가져오거나 Rule에서 가져오는 게 더 완벽함)
        assertThat(discount).isGreaterThan(0); 
    }

    @Test
    @DisplayName("VIP는 최소 주문 기준액에 미달하면 할인이 적용되지 않는다")
    void vip_amount_cut_line_fail() {
        // given
        Member member = new Member("memberVIP", Grade.VIP);
        int minAmount = discountPolicy.getMinOrderAmount();

        // when: 기준 금액에서 10원이 모자란 경우
        int price = minAmount - 10;
        int discount = discountPolicy.discount(member, price);

        // then: 얄짤없이 할인 0원
        assertThat(discount).isEqualTo(0);
    }
    
    @Test
    @DisplayName("일반 회원은 금액 상관없이 할인 없다")
    void basic_member_fail() {
        Member member = new Member("basic", Grade.NORMAL);
        int discount = discountPolicy.discount(member, 20000);
        assertThat(discount).isEqualTo(0);
    }
}