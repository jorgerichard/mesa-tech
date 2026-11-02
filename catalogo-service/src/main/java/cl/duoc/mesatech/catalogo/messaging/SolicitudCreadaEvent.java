package cl.duoc.mesatech.catalogo.messaging;

import java.time.Instant;

public record SolicitudCreadaEvent(
        String eventId,
        Instant occurredAt,
        String titulo,
        String descripcion,
        String categoria,
        String prioridad,
        String solicitanteId,
        String solicitanteNombre) {
}
