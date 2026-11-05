package cl.duoc.mesatech.catalogo.controller;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.duoc.mesatech.catalogo.dto.SolicitudPublicacionRequest;
import cl.duoc.mesatech.catalogo.messaging.SolicitudCreadaEvent;
import cl.duoc.mesatech.catalogo.messaging.SolicitudPublisher;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/internal/v1/solicitudes")
@ConditionalOnProperty(prefix = "app.rabbitmq.legacy-solicitud", name = "enabled", havingValue = "true")
public class SolicitudPublicacionController {
    private final SolicitudPublisher publisher;

    public SolicitudPublicacionController(SolicitudPublisher publisher) {
        this.publisher = publisher;
    }

    @PostMapping
    ResponseEntity<Map<String, Object>> publicar(@Valid @RequestBody SolicitudPublicacionRequest request,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Name") String userName) {
        String eventId = UUID.randomUUID().toString();
        Instant occurredAt = Instant.now();
        publisher.publicar(new SolicitudCreadaEvent(eventId, occurredAt, request.titulo(),
                request.descripcion(), request.categoria(), request.prioridad(), userId, userName));
        return ResponseEntity.accepted().body(Map.of(
                "estado", "ENCOLADA",
                "eventId", eventId,
                "fecha", occurredAt));
    }
}
