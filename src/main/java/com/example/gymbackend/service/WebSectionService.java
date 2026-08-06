package com.example.gymbackend.service;

import com.example.gymbackend.dto.WebSectionDTO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface WebSectionService {
    List<WebSectionDTO> getAllSections(boolean includeInactive);
    WebSectionDTO getSectionById(Long id);
    WebSectionDTO createSection(WebSectionDTO dto, MultipartFile mainImage, List<MultipartFile> carouselFiles);
    WebSectionDTO updateSection(Long id, WebSectionDTO dto, MultipartFile mainImage, List<MultipartFile> carouselFiles);
    void deleteSection(Long id);
    void updateSectionOrder(List<Long> orderedIds);
}
