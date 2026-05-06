package com.example.gymbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "membership_plans")
@Getter
@Setter
public class MembershipPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Double priceAmount;

    @Column(name = "duration_months")
    private Integer durationMonths;

    @Column(name = "is_promotion")
    private Boolean isPromotion;
}
