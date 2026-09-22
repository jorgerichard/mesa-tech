package cl.duoc.mesatech.solicitudes.dto;

import jakarta.validation.constraints.NotNull;
import cl.duoc.mesatech.solicitudes.model.EstadoSolicitud;

public record CambiarEstadoRequest(@NotNull EstadoSolicitud estado, String comentario) {}
