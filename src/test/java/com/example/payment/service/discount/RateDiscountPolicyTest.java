package com.example.payment.service.discount;

import com.example.payment.domain.Grade;
import com.example.payment.domain.Member;
import com.example.payment.service.DiscountPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RateDiscountPolicyTest {

    // 테스트할 대상을 직접 생성 (스프링 없이 순수 자바 코드로 테스트 -> 속도가 엄청 빠름)
    DiscountPolicy discountPolicy = new RateDiscountPolicy();

    @Test
    @DisplayName("VVIP는 10% 할인이 적용되어야 한다")
    void vvip_o() {
        Member member = new Member("memberVVIP", Grade.VVIP);
        int discount = discountPolicy.discount(member, 10000);
        assertThat(discount).isEqualTo(1000);
    }

    @Test
    @DisplayName("VVIP가 아니면 할인이 적용되지 않아야 한다")
    void vvip_x() {
        Member member = new Member("memberBASIC", Grade.NORMAL);
        int discount = discountPolicy.discount(member, 10000);
        assertThat(discount).isEqualTo(0);
    }
}