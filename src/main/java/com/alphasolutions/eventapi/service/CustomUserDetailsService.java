package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.model.CustomUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.alphasolutions.eventapi.utils.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final JwtUtil jwtUtil;


    public CustomUserDetailsService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;

    }


    @Override
    public UserDetails loadUserByUsername(String token) throws UsernameNotFoundException {
        Map<String,Object> claim = jwtUtil.extractClaim(token);
        return new CustomUserDetails(claim.get("unique_code").toString());

    }
}
