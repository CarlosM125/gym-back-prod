package com.example.gymbackend.repository;

import com.example.gymbackend.model.MarketingProposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MarketingProposalRepository extends JpaRepository<MarketingProposal, Long> {
    List<MarketingProposal> findAllByOrderByFechaGeneracionDesc();
}
