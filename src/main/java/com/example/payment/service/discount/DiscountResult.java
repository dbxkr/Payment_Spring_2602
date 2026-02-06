package com.example.payment.service.discount;

import com.example.payment.domain.DiscountRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DiscountResult {
    private int totalDiscountAmount;        // 총 할인액
    private List<DiscountRecord> history;   // 상세 내역 히스토리
}