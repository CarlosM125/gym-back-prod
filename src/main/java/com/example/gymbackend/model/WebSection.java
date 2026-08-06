package com.example.gymbackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "web_sections")
@Getter
@Setter
public class WebSection extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @ElementCollection
    @CollectionTable(name = "web_section_carousel_images", joinColumns = @JoinColumn(name = "web_section_id"))
    @Column(name = "image_url")
    private List<String> carouselImages;

    @Column(name = "section_type", nullable = false)
    private String sectionType; // HERO, TEXT_IMAGE, CAROUSEL, TEXT_ONLY

    @Column(name = "order_index")
    private Integer orderIndex;

    @Column(name = "is_active")
    private Boolean isActive = true;
}
