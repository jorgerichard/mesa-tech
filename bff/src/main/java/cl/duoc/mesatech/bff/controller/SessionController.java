package cl.duoc.mesatech.bff.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.duoc.mesatech.bff.service.UserContext;

@RestController
@RequestMapping("/api/v1/session")
public class SessionController {
    @GetMapping
    Map<String, Object> session(@AuthenticationPrincipal Jwt jwt) {
        UserContext user = UserContext.from(jwt);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", user.id());
        response.put("username", user.username());
        response.put("name", user.name());
        response.put("roles", user.roles());
        response.put("scope", jwt.getClaimAsString("scp"));
        return response;
    }
}
