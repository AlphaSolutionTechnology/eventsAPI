package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.InvalidRoleException;
import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.model.Role;
import com.alphasolutions.eventapi.repository.RoleRepository;
import com.alphasolutions.eventapi.utils.JwtUtil;
import org.springframework.stereotype.Service;

import javax.management.relation.InvalidRoleValueException;
import java.util.Map;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;

    public AuthorizationServiceImpl(JwtUtil jwtUtil, RoleRepository roleRepository) {
        this.jwtUtil = jwtUtil;
        this.roleRepository = roleRepository;
    }

    @Override
    public boolean isRoleAdmin(String token) {
        Map<String,Object> verifiedToken = jwtUtil.extractClaim(token);
        if(verifiedToken.get("error") == null) {
            Role role = roleRepository.findById(1L).orElse(null);
            if (role == null) {
                throw new InvalidRoleException("NO such role");
            }
            return verifiedToken.get("role") != null && role.getRole().equals(verifiedToken.get("role"));
        }
        throw new InvalidTokenException("Invalid token");
    }


}
