package cl.duoc.mesatech.solicitudes.dto;

import java.time.Instant;
import java.util.List;
import cl.duoc.mesatech.solicitudes.model.EstadoSolicitud;
import cl.duoc.mesatech.solicitudes.model.Solicitud;

public record SolicitudV2Response(Long id, String titulo, String descripcion, String categoria,
        String prioridad, String solicitante, String operador, EstadoSolicitud estado,
        Instant fechaCreacion, Instant fechaActualizacion, List<HistorialResponse> historial) {
    public static SolicitudV2Response from(Solicitud s) {
        return new SolicitudV2Response(s.getId(), s.getTitulo(), s.getDescripcion(), s.getCategoria(),
                s.getPrioridad(), s.getSolicitanteNombre(), s.getOperadorNombre(), s.getEstado(),
                s.getFechaCreacion(), s.getFechaActualizacion(), s.getHistorial().stream().map(HistorialResponse::from).toList());
    }
}
