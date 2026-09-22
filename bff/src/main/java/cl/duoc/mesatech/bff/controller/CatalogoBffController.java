package cl.duoc.mesatech.bff.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api/v1/catalogo")
public class CatalogoBffController {
    private final RestClient client;

    public CatalogoBffController(@Qualifier("catalogoClient") RestClient client) {
        this.client = client;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_access_as_user')")
    Object listar() {
        return client.get().uri("/internal/v1/catalogo").retrieve().body(Object.class);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    Object crear(@RequestBody Object body) {
        return client.post().uri("/internal/v1/catalogo").body(body).retrieve().body(Object.class);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    Object actualizar(@PathVariable Long id, @RequestBody Object body) {
        return client.put().uri("/internal/v1/catalogo/{id}", id).body(body).retrieve().body(Object.class);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    void eliminar(@PathVariable Long id) {
        client.delete().uri("/internal/v1/catalogo/{id}", id).retrieve().toBodilessEntity();
    }
}
