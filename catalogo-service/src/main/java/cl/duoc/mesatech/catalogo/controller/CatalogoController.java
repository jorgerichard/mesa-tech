package cl.duoc.mesatech.catalogo.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import cl.duoc.mesatech.catalogo.dto.ItemCatalogoRequest;
import cl.duoc.mesatech.catalogo.model.ItemCatalogo;
import cl.duoc.mesatech.catalogo.service.CatalogoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/internal/v1/catalogo")
public class CatalogoController {
    private final CatalogoService service;
    public CatalogoController(CatalogoService service) { this.service = service; }
    @GetMapping List<ItemCatalogo> listar() { return service.listar(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) ItemCatalogo crear(@Valid @RequestBody ItemCatalogoRequest request) { return service.crear(request); }
    @PutMapping("/{id}") ItemCatalogo actualizar(@PathVariable Long id, @Valid @RequestBody ItemCatalogoRequest request) { return service.actualizar(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) void eliminar(@PathVariable Long id) { service.eliminar(id); }
}
