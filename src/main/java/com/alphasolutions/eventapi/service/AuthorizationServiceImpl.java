package com.alphasolutions.eventapi.service;

import com.alphasolutions.eventapi.exception.InvalidRoleException;
import com.alphasolutions.eventapi.exception.InvalidTokenException;
import com.alphasolutions.eventapi.exception.UserNotMatchWithRequestException;
import com.alphasolutions.eventapi.model.entity.Role;
import com.alphasolutions.eventapi.repository.RoleRepository;
import com.alphasolutions.eventapi.utils.JwtUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthorizationServiceImpl implements AuthorizationService {
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final UserServiceImpl userServiceImpl;

    public AuthorizationServiceImpl(JwtUtil jwtUtil, RoleRepository roleRepository, UserService userService, UserServiceImpl userServiceImpl) {
        this.jwtUtil = jwtUtil;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public boolean isRoleAdmin(String token) {
        Map<String,Object> verifiedToken = jwtUtil.extractClaim(token);
        if(verifiedToken.get("error") == null) {
            Role role = roleRepository.findById(1L).orElse(null);
            if (role == null) {
                throw new InvalidRoleException("NO such role");
            }

            return verifiedToken.get("role") != null && role.getRole().equals(verifiedToken.get("role").toString());
        }
        throw new InvalidTokenException("Invalid token");
    }

    @Override
    public boolean isUserSolicitant(String token, String solicitant) {
        Map<String,Object> verifiedToken = jwtUtil.extractClaim(token);
        if(verifiedToken.get("error") == null) {
            String uniqueCode = verifiedToken.get("unique_code").toString();
            if(uniqueCode.equals(solicitant)){
                return true;
            }
            throw new UserNotMatchWithRequestException("Requisição negada");

        }
        throw new InvalidTokenException("Invalid token");
    }

}
