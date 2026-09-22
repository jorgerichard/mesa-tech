package cl.duoc.mesatech.solicitudes.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import cl.duoc.mesatech.solicitudes.dto.*;
import cl.duoc.mesatech.solicitudes.model.Solicitud;
import cl.duoc.mesatech.solicitudes.service.SolicitudService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/internal")
public class SolicitudController {
    private final SolicitudService service;
    public SolicitudController(SolicitudService service) { this.service = service; }

    @PostMapping("/v1/solicitudes") @ResponseStatus(HttpStatus.CREATED)
    Solicitud crear(@Valid @RequestBody CrearSolicitudRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Name") String userName) {
        return service.crear(request, userId, userName);
    }

    @GetMapping("/v1/solicitudes/mias")
    List<SolicitudV1Response> mias(@RequestHeader("X-User-Id") String userId) { return service.mias(userId); }

    @GetMapping("/v1/solicitudes")
    List<SolicitudV1Response> todas() { return service.todas(); }

    @GetMapping("/v1/solicitudes/{id}")
    SolicitudV1Response detalleV1(@PathVariable Long id, @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "X-User-Roles", defaultValue = "") String roles) {
        return service.detalleV1(id, userId, puedeVerTodas(roles));
    }

    @GetMapping("/v2/solicitudes/{id}")
    SolicitudV2Response detalleV2(@PathVariable Long id, @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "X-User-Roles", defaultValue = "") String roles) {
        return service.detalleV2(id, userId, puedeVerTodas(roles));
    }

    @PatchMapping("/v1/solicitudes/{id}/asignacion")
    SolicitudV2Response asignar(@PathVariable Long id, @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Name") String userName) {
        return service.asignar(id, userId, userName);
    }

    @PatchMapping("/v1/solicitudes/{id}/estado")
    SolicitudV2Response cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambiarEstadoRequest request,
            @RequestHeader("X-User-Name") String userName) {
        return service.cambiarEstado(id, request, userName);
    }

    private boolean puedeVerTodas(String roles) {
        return Arrays.stream(roles.split(",")).anyMatch(r -> r.equalsIgnoreCase("OPERADOR") || r.equalsIgnoreCase("ADMINISTRADOR"));
    }
}
