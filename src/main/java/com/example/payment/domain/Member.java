package com.example.payment.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.github.f4b6a3.tsid.TsidCreator;

@Entity
@Getter
@NoArgsConstructor 
public class Member {

    @Id
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Grade grade;

    public Member(String name, Grade grade) {
        this.id = TsidCreator.getTsid256().toLong(); //회원 ID 생성시 TSID 생성방식을 따름.
        this.name = name;
        this.grade = grade;
    }
}