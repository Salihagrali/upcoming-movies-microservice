package com.movies.userservice.user;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String register(@RequestBody KeycloakUser user){
        return userService.registerUser(user);
    }
}
