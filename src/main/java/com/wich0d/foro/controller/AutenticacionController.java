package com.wich0d.foro.controller;

import com.wich0d.foro.domain.usuarios.DatosAutenticaconUsuarios;
import com.wich0d.foro.domain.usuarios.Usuario;
import com.wich0d.foro.infra.seguridad.DatosJWTToken;
import com.wich0d.foro.infra.seguridad.TokenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacionController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;
    @PostMapping
    public ResponseEntity autenticacionUsuario(@RequestBody @Valid DatosAutenticaconUsuarios dau){
        Authentication authToken = new UsernamePasswordAuthenticationToken(dau.email(),dau.password());
        var usuario = authenticationManager.authenticate(authToken);
        var JWTtoken = tokenService.generarToken((Usuario) usuario.getPrincipal());
        return  ResponseEntity.ok(new DatosJWTToken(JWTtoken));
    }
}
