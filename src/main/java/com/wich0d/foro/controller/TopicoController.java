package com.wich0d.foro.controller;

import com.wich0d.foro.topicos.*;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
                topico.getId(), topico.getTitulo(),topico.getMensaje(),topico.getFecha(),topico.getStatus(),topico.getAutor(),topico.getCurso()
        ));
    }
    @GetMapping
    public ResponseEntity<Page<DatosListadoTopicos>> mostrarTopicos(@PageableDefault(size=10)Pageable paginacion){
        return  ResponseEntity.ok(topicoRepository.findAllByOrderByFechaAsc(paginacion).map(DatosListadoTopicos::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosListadoTopicos> mostrarTopicoEspecifico( @PathVariable Long id){
        Topico topico = topicoRepository.getReferenceById(id);

        return ResponseEntity.ok(new DatosListadoTopicos(
                topico.getTitulo(),topico.getMensaje(),topico.getFecha(),topico.getStatus(),topico.getAutor(),topico.getCurso()
        ));
    }
    @PutMapping
    @Transactional
    public  ResponseEntity<DatosRespuestaTopico> actualizarTopico(@RequestBody @Valid DatosActualizarTopico datos){
        Topico topico = topicoRepository.getReferenceById(datos.id());
        topico.actualizarDatos(datos);
        return ResponseEntity.ok(new DatosRespuestaTopico(
                topico.getId(), topico.getTitulo(),topico.getMensaje(),topico.getFecha(),topico.getStatus(),topico.getAutor(),topico.getCurso()
        ));
    }
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity eliminarTopico(@PathVariable Long id){
        topicoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
