package com.example.gymbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSectionDTO {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private List<String> carouselImages;
    private String sectionType;
    private Integer orderIndex;
    private Boolean isActive;
}
