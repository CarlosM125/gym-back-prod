package com.example.gymbackend.repository;

import com.example.gymbackend.model.SystemAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemAccountRepository extends JpaRepository<SystemAccount, Long> {
    Optional<SystemAccount> findByUsername(String username);
}
