package cl.duoc.mesatech.bff.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import cl.duoc.mesatech.bff.service.UserContext;

@RestController
@RequestMapping("/api")
public class SolicitudBffController {
    private final RestClient solicitudesClient;
    private final RestClient catalogoClient;

    public SolicitudBffController(
            @Qualifier("solicitudesClient") RestClient solicitudesClient,
            @Qualifier("catalogoClient") RestClient catalogoClient) {
        this.solicitudesClient = solicitudesClient;
        this.catalogoClient = catalogoClient;
    }

    @PostMapping("/v1/solicitudes")
    @PreAuthorize("hasAuthority('SCOPE_access_as_user') and (hasRole('CLIENTE') or hasRole('OPERADOR') or hasRole('ADMINISTRADOR'))")
    ResponseEntity<Object> crear(@RequestBody Object body, @AuthenticationPrincipal Jwt jwt) {
        return catalogoClient.post().uri("/internal/v1/solicitudes").headers(h -> userHeaders(h, jwt))
                .body(body).retrieve().toEntity(Object.class);
    }

    @GetMapping("/v1/solicitudes/mias")
    @PreAuthorize("hasAuthority('SCOPE_access_as_user')")
    Object mias(@AuthenticationPrincipal Jwt jwt) {
        return solicitudesClient.get().uri("/internal/v1/solicitudes/mias").headers(h -> userHeaders(h, jwt))
                .retrieve().body(new ParameterizedTypeReference<Object>() {});
    }

    @GetMapping("/v1/solicitudes")
    @PreAuthorize("hasAnyRole('OPERADOR','ADMINISTRADOR')")
    Object todas(@AuthenticationPrincipal Jwt jwt) {
        return solicitudesClient.get().uri("/internal/v1/solicitudes").headers(h -> userHeaders(h, jwt))
                .retrieve().body(new ParameterizedTypeReference<Object>() {});
    }

    @GetMapping("/v1/solicitudes/{id}")
    @PreAuthorize("hasAuthority('SCOPE_access_as_user')")
    Object detalleV1(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return solicitudesClient.get().uri("/internal/v1/solicitudes/{id}", id).headers(h -> userHeaders(h, jwt))
                .retrieve().body(Object.class);
    }

    @GetMapping("/v2/solicitudes/{id}")
    @PreAuthorize("hasAuthority('SCOPE_access_as_user')")
    Object detalleV2(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return solicitudesClient.get().uri("/internal/v2/solicitudes/{id}", id).headers(h -> userHeaders(h, jwt))
                .retrieve().body(Object.class);
    }

    @PatchMapping("/v1/solicitudes/{id}/estado")
    @PreAuthorize("hasAnyRole('OPERADOR','ADMINISTRADOR')")
    Object cambiarEstado(@PathVariable Long id, @RequestBody Object body, @AuthenticationPrincipal Jwt jwt) {
        return solicitudesClient.patch().uri("/internal/v1/solicitudes/{id}/estado", id).headers(h -> userHeaders(h, jwt))
                .body(body).retrieve().body(Object.class);
    }

    @PatchMapping("/v1/solicitudes/{id}/asignacion")
    @PreAuthorize("hasAnyRole('OPERADOR','ADMINISTRADOR')")
    Object asignar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return solicitudesClient.patch().uri("/internal/v1/solicitudes/{id}/asignacion", id).headers(h -> userHeaders(h, jwt))
                .retrieve().body(Object.class);
    }

    private void userHeaders(org.springframework.http.HttpHeaders headers, Jwt jwt) {
        UserContext user = UserContext.from(jwt);
        headers.set("X-User-Id", user.id());
        headers.set("X-User-Name", user.name());
        headers.set("X-User-Roles", String.join(",", user.roles()));
    }
}
