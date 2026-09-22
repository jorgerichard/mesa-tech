package cl.duoc.mesatech.solicitudes.dto;

import java.time.Instant;
import cl.duoc.mesatech.solicitudes.model.EstadoSolicitud;
import cl.duoc.mesatech.solicitudes.model.HistorialEstado;

public record HistorialResponse(EstadoSolicitud estadoAnterior, EstadoSolicitud estadoNuevo,
        String usuario, String comentario, Instant fecha) {
    public static HistorialResponse from(HistorialEstado h) {
        return new HistorialResponse(h.getEstadoAnterior(), h.getEstadoNuevo(), h.getUsuario(), h.getComentario(), h.getFecha());
    }
}
