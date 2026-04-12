package com.example.gymbackend.security;

import com.example.gymbackend.model.SystemAccount;
import com.example.gymbackend.repository.SystemAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SystemAccountRepository systemAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemAccount account = systemAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + account.getRole().name());
        
        return new User(account.getUsername(), account.getPasswordHash(), Collections.singleton(authority));
    }
}
