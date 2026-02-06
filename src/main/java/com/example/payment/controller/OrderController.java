package com.example.payment.controller;

import com.example.payment.domain.Member;
import com.example.payment.domain.Order;
import com.example.payment.domain.PaymentMethod;
import com.example.payment.repository.MemberRepository;
import com.example.payment.service.OrderService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final MemberRepository memberRepository;

    // [POST] 주문 생성 요청
    @PostMapping
    public Order createOrder(@RequestBody OrderRequest request) {
        return orderService.createOrder(
                request.memberId,
                request.itemName,
                request.itemPrice,
                request.paymentMethod
        );
    }

    @Data
    static class OrderRequest {
        private Long memberId;
        private String itemName;
        private int itemPrice;
        private PaymentMethod paymentMethod; 
    }

    // @org.springframework.context.annotation.Profile("local") 
    @GetMapping("/testmembers")
    public List<Member> getTestMembers() {
        return memberRepository.findTop3ByOrderByIdAsc();
    }
}