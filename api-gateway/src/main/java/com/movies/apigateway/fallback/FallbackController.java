package com.movies.apigateway.fallback;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@RestController
public class FallbackController {

    @GetMapping("/fallback")
    public ResponseEntity<Map<String,Object>> fallback(@AuthenticationPrincipal Jwt jwt){
        Map<String,Object> response = new HashMap<>();
        response.put("status","SERVICE_UNAVAILABLE");
        response.put("message","The service you are trying to reach is currently unavailable. Please try again later.");
        response.put("fallback","true");

        if(jwt != null){
            response.put("user",jwt.getClaim("preffered_username"));
        }

        return ResponseEntity
                .status(SERVICE_UNAVAILABLE)
                .body(response);
    }
}
