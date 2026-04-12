package com.example.gymbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_branch_id")
    private Branch homeBranch;

    @Column(name = "pin_zkteco", unique = true, nullable = false)
    private Integer pinZkteco;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(name = "document_id", length = 50, unique = true)
    private String documentId;

    @Column(length = 150)
    private String email;

    @Column(name = "profile_image_url", length = 500)
    private String profileImageUrl;
}
