package com.wich0d.foro.controller;

import com.wich0d.foro.topicos.DatosRespuestaTopico;
import com.wich0d.foro.topicos.DatosSubirTopico;
import com.wich0d.foro.topicos.Topico;
import com.wich0d.foro.topicos.TopicoRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topicos")
public class TopicoController {
    @Autowired
    private TopicoRepository topicoRepository;
    @PostMapping
    @Transactional
    public ResponseEntity subirTopico(@RequestBody @Valid DatosSubirTopico datos){
        Topico topico = topicoRepository.save(new Topico(datos));
        return ResponseEntity.ok(new DatosRespuestaTopico(
                topico.getId(), topico.getTitulo(),topico.getMensaje(),topico.getFecha(),topico.getStatus(),topico.getAutor()
        ));
    }
}
