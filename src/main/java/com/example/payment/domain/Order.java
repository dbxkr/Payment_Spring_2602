package com.example.payment.domain;

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

    public Order(Member member, String itemName, int itemPrice, int discountPrice) {
        this.id = TsidCreator.getTsid256().toLong();
        this.member = member;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.discountPrice = discountPrice;
        this.memberGradeSnapshot = member.getGrade(); 
    }
    
    public int calculateFinalPrice() {
        return itemPrice - discountPrice;
    }
}