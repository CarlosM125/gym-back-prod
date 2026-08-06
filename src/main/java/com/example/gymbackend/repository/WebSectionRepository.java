package com.example.gymbackend.repository;

import com.example.gymbackend.model.WebSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WebSectionRepository extends JpaRepository<WebSection, Long> {
    List<WebSection> findAllByOrderByOrderIndexAsc();
    List<WebSection> findByIsActiveTrueOrderByOrderIndexAsc();
}
