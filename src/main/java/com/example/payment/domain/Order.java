package com.example.payment.domain;

import java.util.ArrayList;
import java.util.List;

import com.github.f4b6a3.tsid.TsidCreator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
@Table(name = "orders")
public class Order {

    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String itemName;
    private int itemPrice;

    @Enumerated(EnumType.STRING)
    private Grade memberGradeSnapshot; 
    private int discountPrice; 

    @ElementCollection 
    @CollectionTable(
        name = "order_discount_history", 
        joinColumns = @JoinColumn(name = "order_id") // 외래키
    )
    private List<DiscountRecord> discountRecords = new ArrayList<>();

    public Order(Member member, String itemName, int itemPrice, 
                 int discountPrice, List<DiscountRecord> records) { // 파라미터 추가
        this.id = TsidCreator.getTsid256().toLong();
        this.member = member;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.discountPrice = discountPrice;
        this.memberGradeSnapshot = member.getGrade();
        
        // 이력 저장
        this.discountRecords.addAll(records);
    }
    
    public int calculateFinalPrice() {
        return itemPrice - discountPrice;
    }
}