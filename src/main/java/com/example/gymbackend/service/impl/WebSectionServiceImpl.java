package com.example.gymbackend.service.impl;

import com.example.gymbackend.dto.WebSectionDTO;
import com.example.gymbackend.model.WebSection;
import com.example.gymbackend.repository.WebSectionRepository;
import com.example.gymbackend.service.CloudinaryService;
import com.example.gymbackend.service.WebSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WebSectionServiceImpl implements WebSectionService {

    @Autowired
    private WebSectionRepository webSectionRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public List<WebSectionDTO> getAllSections(boolean includeInactive) {
        List<WebSection> sections = includeInactive 
            ? webSectionRepository.findAllByOrderByOrderIndexAsc() 
            : webSectionRepository.findByIsActiveTrueOrderByOrderIndexAsc();
            
        return sections.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public WebSectionDTO getSectionById(Long id) {
        return webSectionRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Section not found: " + id));
    }

    @Override
    public WebSectionDTO createSection(WebSectionDTO dto, MultipartFile mainImage, List<MultipartFile> carouselFiles) {
        WebSection section = new WebSection();
        section.setTitle(dto.getTitle());
        section.setDescription(dto.getDescription());
        section.setSectionType(dto.getSectionType());
        section.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : true);
        
        // Auto-assign order if not provided
        if (dto.getOrderIndex() == null) {
            long count = webSectionRepository.count();
            section.setOrderIndex((int) count);
        } else {
            section.setOrderIndex(dto.getOrderIndex());
        }

        try {
            if (mainImage != null && !mainImage.isEmpty()) {
                String url = cloudinaryService.uploadImage(mainImage);
                section.setImageUrl(url);
            }

            if (carouselFiles != null && !carouselFiles.isEmpty()) {
                List<String> urls = new ArrayList<>();
                for (MultipartFile file : carouselFiles) {
                    if (!file.isEmpty()) {
                        urls.add(cloudinaryService.uploadImage(file));
                    }
                }
                section.setCarouselImages(urls);
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error al subir imagen: " + e.getMessage());
        }

        return mapToDTO(webSectionRepository.save(section));
    }

    @Override
    public WebSectionDTO updateSection(Long id, WebSectionDTO dto, MultipartFile mainImage, List<MultipartFile> carouselFiles) {
        WebSection section = webSectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Section not found: " + id));

        if (dto.getTitle() != null) section.setTitle(dto.getTitle());
        if (dto.getDescription() != null) section.setDescription(dto.getDescription());
        if (dto.getSectionType() != null) section.setSectionType(dto.getSectionType());
        if (dto.getIsActive() != null) section.setIsActive(dto.getIsActive());
        if (dto.getOrderIndex() != null) section.setOrderIndex(dto.getOrderIndex());

        try {
            if (mainImage != null && !mainImage.isEmpty()) {
                if (section.getImageUrl() != null && section.getImageUrl().contains("cloudinary.com")) {
                    try {
                        cloudinaryService.deleteImage(section.getImageUrl());
                    } catch (Exception ignored) {}
                }
                section.setImageUrl(cloudinaryService.uploadImage(mainImage));
            }

            if (carouselFiles != null && !carouselFiles.isEmpty()) {
                // Delete old carousel images if we are replacing them
                if (section.getCarouselImages() != null) {
                    for (String url : section.getCarouselImages()) {
                        if (url.contains("cloudinary.com")) {
                            try {
                                cloudinaryService.deleteImage(url);
                            } catch (Exception ignored) {}
                        }
                    }
                }
                
                List<String> urls = new ArrayList<>();
                for (MultipartFile file : carouselFiles) {
                    if (!file.isEmpty()) {
                        urls.add(cloudinaryService.uploadImage(file));
                    }
                }
                section.setCarouselImages(urls);
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error al subir imagen: " + e.getMessage());
        }

        return mapToDTO(webSectionRepository.save(section));
    }

    @Override
    public void deleteSection(Long id) {
        WebSection section = webSectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Section not found: " + id));
                
        if (section.getImageUrl() != null && section.getImageUrl().contains("cloudinary.com")) {
            try {
                cloudinaryService.deleteImage(section.getImageUrl());
            } catch (Exception ignored) {}
        }
        
        if (section.getCarouselImages() != null) {
            for (String url : section.getCarouselImages()) {
                if (url.contains("cloudinary.com")) {
                    try {
                        cloudinaryService.deleteImage(url);
                    } catch (Exception ignored) {}
                }
            }
        }
        
        webSectionRepository.delete(section);
    }
    
    @Override
    public void updateSectionOrder(List<Long> orderedIds) {
        for (int i = 0; i < orderedIds.size(); i++) {
            Long id = orderedIds.get(i);
            webSectionRepository.findById(id).ifPresent(section -> {
                section.setOrderIndex(orderedIds.indexOf(id));
                webSectionRepository.save(section);
            });
        }
    }

    private WebSectionDTO mapToDTO(WebSection section) {
        return WebSectionDTO.builder()
                .id(section.getId())
                .title(section.getTitle())
                .description(section.getDescription())
                .imageUrl(section.getImageUrl())
                .carouselImages(section.getCarouselImages())
                .sectionType(section.getSectionType())
                .orderIndex(section.getOrderIndex())
                .isActive(section.getIsActive())
                .build();
    }
}
