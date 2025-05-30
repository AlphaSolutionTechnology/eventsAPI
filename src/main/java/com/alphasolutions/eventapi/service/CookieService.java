package com.alphasolutions.eventapi.service;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

@Service
public class CookieService {
    public ResponseCookie createCookie(String eventToken) {
        return ResponseCookie
                .from("eventToken", eventToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(7 * 24 * 60 * 60)
                .build();
    }

    public ResponseCookie deleteTokenCookie() {

        return ResponseCookie
                .from("eventToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .sameSite("Strict")
                .maxAge(0)
                .build();
    }
}
