package com.wich0d.foro.domain.topicos;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DatosActualizarTopico(
        @NotNull
        Long id,
        String titulo,
        String mensaje,
        LocalDateTime fecha,
        EstadosTopico status,
        String autor,
        Cursos curso
) {

}
