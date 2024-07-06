package com.wich0d.foro.topicos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record DatosSubirTopico(

        Long id,
        @NotBlank
        String titulo,
        @NotBlank
        String mensaje,
        @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
        LocalDateTime fecha,
        EstadosTopico status,
        @NotBlank
        String autor
) {
}
