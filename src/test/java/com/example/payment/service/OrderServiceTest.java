package com.example.payment.service;

import com.example.payment.domain.DiscountRecord;
import com.example.payment.domain.Grade;
import com.example.payment.domain.Member;
import com.example.payment.domain.Order;
import com.example.payment.domain.PaymentMethod;
import com.example.payment.repository.MemberRepository;
import com.example.payment.service.discount.FixDiscountPolicy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired MemberRepository memberRepository;
    @Autowired FixDiscountPolicy fixDiscountPolicy;

    // ==========================================
    // 1. VVIP 시나리오
    // ==========================================

    @Test
    @DisplayName("[VVIP+포인트] 등급할인(10%) + 포인트할인(5%,올림) 중복 적용")
    void vvip_with_point() {
        Member member = saveMember("vvipUser", Grade.VVIP);
        int itemPrice = 10050;

        Order order = orderService.createOrder(member.getId(), "RTX 5090", itemPrice, PaymentMethod.POINT);

        // 검증: (10050 * 0.1 = 1005) + (9045 * 0.05 = 452.25 -> 453) = 1458
        assertThat(order.getDiscountPrice()).isEqualTo(1458);
        
        assertThat(order.getDiscountRecords())
                .extracting("policyName", "discountAmount")
                .containsExactly(
                        tuple("VVIP 등급 할인(10%)", 1005),
                        tuple("포인트 결제 추가 할인(5%)", 453)
                );

        printReceipt(order, "VVIP + 포인트 중복 할인");
    }

    @Test
    @DisplayName("[VVIP+카드] 등급할인(10%)만 적용")
    void vvip_with_card() {
        Member member = saveMember("vvipUser", Grade.VVIP);
        Order order = orderService.createOrder(member.getId(), "일반템", 20000, PaymentMethod.CREDIT_CARD);

        assertThat(order.getDiscountPrice()).isEqualTo(2000);
        
        printReceipt(order, "VVIP + 카드 결제");
    }

    // ==========================================
    // 2. VIP 시나리오
    // ==========================================

    @Test
    @DisplayName("[VIP+포인트] 최소금액 충족: 고정할인 + 포인트")
    void vip_over_threshold() {
        Member member = saveMember("vipUser", Grade.VIP);
        int itemPrice = fixDiscountPolicy.getMinOrderAmount(); // 딱 커트라인

        Order order = orderService.createOrder(member.getId(), "비싼거", itemPrice, PaymentMethod.POINT);

        assertThat(order.getDiscountRecords()).hasSize(2);
        
        printReceipt(order, "VIP (최소금액 충족) + 포인트");
    }

    @Test
    @DisplayName("[VIP+포인트] 최소금액 미달: 고정할인 제외, 포인트만 적용")
    void vip_under_threshold() {
        Member member = saveMember("vipUser", Grade.VIP);
        int minAmount = fixDiscountPolicy.getMinOrderAmount();
        int itemPrice = minAmount - 100; // 100원 미달

        Order order = orderService.createOrder(member.getId(), "애매한거", itemPrice, PaymentMethod.POINT);

        // 포인트 할인만 적용됐는지 검증
        assertThat(order.getDiscountRecords()).hasSize(1);
        assertThat(order.getDiscountRecords().get(0).getPolicyName()).contains("포인트");

        printReceipt(order, "VIP (최소금액 미달) + 포인트");
    }

    @Test
    @DisplayName("[VIP+포인트] 초저가(1000원): 포인트 할인만 적용")
    void vip_low_price() {
        Member member = saveMember("vipUser", Grade.VIP);
        Order order = orderService.createOrder(member.getId(), "껌", 1000, PaymentMethod.POINT);

        assertThat(order.getDiscountPrice()).isEqualTo(50); // 1000 * 5% = 50
        
        printReceipt(order, "VIP (초저가) + 포인트");
    }

    // ==========================================
    // 3. 일반(Normal) 시나리오
    // ==========================================

    @Test
    @DisplayName("[Normal+포인트] 포인트 할인만 적용")
    void normal_with_point() {
        Member member = saveMember("normalUser", Grade.NORMAL);
        Order order = orderService.createOrder(member.getId(), "보통템", 10000, PaymentMethod.POINT);

        assertThat(order.getDiscountPrice()).isEqualTo(500);
        
        printReceipt(order, "일반회원 + 포인트");
    }

    @Test
    @DisplayName("[Normal+카드] 할인 없음")
    void normal_with_card() {
        Member member = saveMember("normalUser", Grade.NORMAL);
        Order order = orderService.createOrder(member.getId(), "보통템", 10000, PaymentMethod.CREDIT_CARD);

        assertThat(order.getDiscountPrice()).isEqualTo(0);
        
        printReceipt(order, "일반회원 + 카드 (할인 없음)");
    }

    // ==========================================
    // 헬퍼 메서드 (데이터 생성 & 영수증 출력)
    // ==========================================

    private Member saveMember(String name, Grade grade) {
        Member member = new Member(name, grade);
        memberRepository.save(member);
        return member;
    }

    // ⭐ 요청하신 영수증 출력 메서드
    private void printReceipt(Order order, String testTitle) {
        System.out.println("\n==================================================");
        System.out.println(" 🛒 TEST CASE: " + testTitle);
        System.out.println("==================================================");
        System.out.println(" - 상품명   : " + order.getItemName());
        System.out.println(" - 주문금액 : " + String.format("%,d원", order.getItemPrice()));
        System.out.println(" - 할인금액 : " + String.format("%,d원", order.getDiscountPrice()));
        System.out.println(" - 최종결제 : " + String.format("%,d원", order.calculateFinalPrice()));
        System.out.println("--------------------------------------------------");
        System.out.println(" [상세 할인 내역]");
        
        if (order.getDiscountRecords().isEmpty()) {
            System.out.println("   (할인 적용 내역 없음)");
        } else {
            for (DiscountRecord record : order.getDiscountRecords()) {
                System.out.println("   ✅ " + record.getPolicyName() + " : -" + String.format("%,d원", record.getDiscountAmount()));
            }
        }
        System.out.println("==================================================\n");
    }
}