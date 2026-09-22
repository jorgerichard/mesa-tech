package cl.duoc.mesatech.solicitudes.dto;

import jakarta.validation.constraints.NotBlank;

public record CrearSolicitudRequest(
        @NotBlank String titulo,
        @NotBlank String descripcion,
        @NotBlank String categoria,
        @NotBlank String prioridad) {}
