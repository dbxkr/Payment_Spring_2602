package com.example.payment.service.discount;

import com.example.payment.domain.DiscountRecord;
import com.example.payment.domain.Member;
import com.example.payment.domain.PaymentMethod;
import com.example.payment.service.DiscountPolicy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Primary
public class CompositeDiscountPolicy implements DiscountPolicy {

    private final List<DiscountPolicy> basePolicies;
    private final List<DiscountPolicy> additionalPolicies;

    //모든 할인정책을 불러와 순차적으로 적용하기 위한 리스트
    public CompositeDiscountPolicy(List<DiscountPolicy> allPolicies) {
        Map<DiscountType, List<DiscountPolicy>> policyMap = allPolicies.stream()
                .filter(p -> !(p instanceof CompositeDiscountPolicy)) // 자기 자신 제외
                .collect(Collectors.groupingBy(DiscountPolicy::getDiscountType));

        this.basePolicies = policyMap.getOrDefault(DiscountType.BASE, List.of());
        this.additionalPolicies = policyMap.getOrDefault(DiscountType.ADDITIONAL, List.of());
    }

    @Override
    public DiscountResult discount(Member member, int originalPrice, PaymentMethod method) {
        int totalDiscount = 0;
        List<DiscountRecord> allRecords = new ArrayList<>(); 

        // 기본 할인
        for (DiscountPolicy policy : basePolicies) {
            DiscountResult result = policy.discount(member, originalPrice, method);
            totalDiscount += result.getTotalDiscountAmount();
            allRecords.addAll(result.getHistory()); // 내역 합치기
        }

        // 할인 금액 음수 방지(fix는 내부에 이미 안전장치가 있으므로 vvip할인율이 100을 넘길때만 작동)
        if (totalDiscount >= originalPrice) {
            return new DiscountResult(originalPrice, allRecords);
        }

        // 추가 할인
        int reducedPrice = originalPrice - totalDiscount;
        
        for (DiscountPolicy policy : additionalPolicies) {
            DiscountResult result = policy.discount(member, reducedPrice, method);
            totalDiscount += result.getTotalDiscountAmount();
            allRecords.addAll(result.getHistory()); // 내역 합치기
        }

        // 할인 금액 음수 방지2
        if (totalDiscount >= originalPrice) {
            return new DiscountResult(originalPrice, allRecords);
        }

        // 최종 할인금액 리턴
        return new DiscountResult(totalDiscount, allRecords);
    }

    @Override
    public DiscountType getDiscountType() {
        return DiscountType.BASE;
    }
}