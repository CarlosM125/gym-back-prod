package com.example.gymbackend.controller;

import com.example.gymbackend.payload.response.ApiResponse;
import com.example.gymbackend.dto.WebSectionDTO;
import com.example.gymbackend.service.WebSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/v1/website/sections")
public class WebSectionController {

    @Autowired
    private WebSectionService webSectionService;

    // Public endpoint for the website
    @GetMapping
    public ResponseEntity<ApiResponse<List<WebSectionDTO>>> getActiveSections() {
        return ResponseEntity.ok(ApiResponse.success(webSectionService.getAllSections(false), "Secciones recuperadas"));
    }

    // Admin endpoint to view all
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN_TI', 'ADMIN_GYM')")
    public ResponseEntity<ApiResponse<List<WebSectionDTO>>> getAllSections() {
        return ResponseEntity.ok(ApiResponse.success(webSectionService.getAllSections(true), "Todas las secciones recuperadas"));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN_TI', 'ADMIN_GYM')")
    public ResponseEntity<ApiResponse<WebSectionDTO>> createSection(
            @RequestPart("data") WebSectionDTO dto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "carouselImages", required = false) List<MultipartFile> carouselImages) {
        
        WebSectionDTO created = webSectionService.createSection(dto, mainImage, carouselImages);
        return ResponseEntity.ok(ApiResponse.success(created, "Sección creada exitosamente"));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN_TI', 'ADMIN_GYM')")
    public ResponseEntity<ApiResponse<WebSectionDTO>> updateSection(
            @PathVariable Long id,
            @RequestPart("data") WebSectionDTO dto,
            @RequestPart(value = "mainImage", required = false) MultipartFile mainImage,
            @RequestPart(value = "carouselImages", required = false) List<MultipartFile> carouselImages) {
        
        WebSectionDTO updated = webSectionService.updateSection(id, dto, mainImage, carouselImages);
        return ResponseEntity.ok(ApiResponse.success(updated, "Sección actualizada exitosamente"));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_TI', 'ADMIN_GYM')")
    public ResponseEntity<ApiResponse<Void>> deleteSection(@PathVariable Long id) {
        webSectionService.deleteSection(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Sección eliminada"));
    }

    @PutMapping("/order")
    @PreAuthorize("hasAnyRole('ADMIN_TI', 'ADMIN_GYM')")
    public ResponseEntity<ApiResponse<Void>> updateOrder(@RequestBody List<Long> orderedIds) {
        webSectionService.updateSectionOrder(orderedIds);
        return ResponseEntity.ok(ApiResponse.success(null, "Orden actualizado"));
    }
}
