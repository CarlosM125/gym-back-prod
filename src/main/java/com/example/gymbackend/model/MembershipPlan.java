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

    // Legacy column from older schema version. Required because production DB still has this column as NOT NULL.
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays = 0;

    @Column(name = "is_promotion")
    private Boolean isPromotion;

    @PrePersist
    @PreUpdate
    public void syncLegacyFields() {
        if (this.durationMonths != null) {
            this.durationDays = this.durationMonths * 30;
        } else {
            this.durationDays = 0;
        }
    }
}
