package cl.duoc.mesatech.solicitudes.dto;

import java.time.Instant;
import cl.duoc.mesatech.solicitudes.model.EstadoSolicitud;
import cl.duoc.mesatech.solicitudes.model.Solicitud;

public record SolicitudV1Response(Long id, String titulo, String descripcion, String categoria,
        String prioridad, String solicitante, EstadoSolicitud estado, Instant fechaCreacion) {
    public static SolicitudV1Response from(Solicitud s) {
        return new SolicitudV1Response(s.getId(), s.getTitulo(), s.getDescripcion(), s.getCategoria(),
                s.getPrioridad(), s.getSolicitanteNombre(), s.getEstado(), s.getFechaCreacion());
    }
}
