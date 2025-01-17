package com.alphasolutions.eventapi.controller;

import com.google.auth.oauth2.TokenVerifier;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LoginController {

    public static final String clientID = "937916098858-8ekrflam5ad65379jqocah9l2dlrjtrq.apps.googleusercontent.com";

    @PostMapping("/google")
    public Map<String,Object> authenticateWithGoogle(@RequestBody Map<String,String> body){
        String token = body.get("token");
        try{
            TokenVerifier tokenVerifier = TokenVerifier.newBuilder().setAudience(clientID).build();
            var verifiedToken = tokenVerifier.verify(token);

            var email = verifiedToken.getPayload().get("email");
            var name = verifiedToken.getPayload().get("name");
            System.out.println(verifiedToken.getPayload());
            return Map.of("message", "Successfully logged in","email", email,"name", name);
        } catch (Exception e) {
            throw new RuntimeException("Failed to authenticate with Google",e);
        }

    }

}
