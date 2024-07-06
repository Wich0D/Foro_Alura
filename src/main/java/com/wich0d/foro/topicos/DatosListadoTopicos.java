package com.wich0d.foro.topicos;

import java.time.LocalDateTime;

public record DatosListadoTopicos(
    String titulo,
    String mensaje,
    LocalDateTime fecha,
    EstadosTopico estado,
    String autor,
    Cursos curso
) {
    public  DatosListadoTopicos(Topico topico){
        this(
                topico.getTitulo(),
                topico.getMensaje(),
                topico.getFecha(),
                topico.getStatus(),
                topico.getAutor(),
                topico.getCurso()
        );
    }
}
