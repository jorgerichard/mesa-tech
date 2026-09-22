package cl.duoc.mesatech.catalogo.dto;

import cl.duoc.mesatech.catalogo.model.TipoCatalogo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ItemCatalogoRequest(@NotBlank String codigo, @NotBlank String nombre,
        @NotNull TipoCatalogo tipo, boolean activo) {}
