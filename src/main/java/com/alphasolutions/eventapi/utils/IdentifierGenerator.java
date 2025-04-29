package com.alphasolutions.eventapi.utils;

import com.alphasolutions.eventapi.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class IdentifierGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();
    private final UserRepository userRepository;

    public IdentifierGenerator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static String generateIdentity(int length) {
        StringBuilder code = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            code.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    public String generateUniqueCode() {
        String uniqueCode;
        do{
            uniqueCode = IdentifierGenerator.generateIdentity(6);
        }while (userRepository.existsByUniqueCode(uniqueCode));
        return uniqueCode;
    }

}