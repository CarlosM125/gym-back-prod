package com.example.gymbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a gym member/client (the person who buys memberships).
 * This is NOT a system user — see User.java for login accounts.
 */
@Entity
@Table(name = "customers")
@Getter
@Setter
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "document_id", unique = true)
    private String documentId;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "pin_zkteco", unique = true)
    private Integer pinZkteco;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "status", length = 20)
    private String status = "ACTIVE"; // ACTIVE, INACTIVE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_branch_id")
    private Branch homeBranch;
}
