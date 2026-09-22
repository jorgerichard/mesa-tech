package cl.duoc.mesatech.bff.service;

import java.util.List;

import org.springframework.security.oauth2.jwt.Jwt;

public record UserContext(String id, String username, String name, List<String> roles) {
    public static UserContext from(Jwt jwt) {
        List<String> roles = jwt.getClaimAsStringList("roles");
        return new UserContext(
                jwt.getSubject(),
                value(jwt, "preferred_username", jwt.getSubject()),
                value(jwt, "name", value(jwt, "preferred_username", jwt.getSubject())),
                roles == null || roles.isEmpty() ? List.of("CLIENTE") : roles);
    }

    private static String value(Jwt jwt, String claim, String fallback) {
        String value = jwt.getClaimAsString(claim);
        return value == null || value.isBlank() ? fallback : value;
    }
}
