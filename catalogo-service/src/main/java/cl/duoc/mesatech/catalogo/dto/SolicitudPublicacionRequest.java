package cl.duoc.mesatech.catalogo.dto;

import jakarta.validation.constraints.NotBlank;

public record SolicitudPublicacionRequest(
        @NotBlank String titulo,
        @NotBlank String descripcion,
        @NotBlank String categoria,
        @NotBlank String prioridad) {
}
