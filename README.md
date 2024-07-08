# FOROHUB


## Descripcion
Este es el 3er desafio del curso ONE de ORACLE en colaboración con ALURA LATAM en la especialización de BACKEND utilizando java Spring.
El desafio consiste en crear un foro en el que se puedan subir topicos, editarlos, eliminarlos y buscarlos. Ademas de implementar otro tipo de funcionalidades.

## Tópicos
Los tópicos son la base del foro, es donde podemos dar a conocer nuestras dudas sibre algún curso y así esperar a que alguien pueda ayudarnos.

La estructura de los tópcios utilizados en este proyecto es:
- titulo
- mensaje
- fecha de creacion
- estado de tópico
- autor
- curso

## CRUD
Para poder gestionar los tópicos del foro, aplicamos un sistema CRUD que nos ayudará a poder manejar estos datos de una forma eficaz y sencilla de comprender.

Para poder conectar la aplicación a un servidor se esta utilizando la dependencia **Spring web** y la clase _topicoController_ que es la clase correspondiente a la implementación y gestión de el CRUD.
Se utiliza un repositorio utilizando la dependencia **JPA** para poder declarar funciones que nos ayuden a hacer consultas en la abse de datos.
Cada uno de los métodos implementados retorna un valor tipo *ResponseEntity* y en algunos casos acompañados de otras clases cuya nomenclatura comienza con "_Datos..._", esta nomenclatura se está utlizando para aquellas clases DTO cuyo objetivo es mostrar los datos de salida que se obtuvieron de las operaciones que se realizaron.

### POST 
Comenzamos por el *POST*, este es el encargado de la escritura de la base de datos, es decir, con *POST* añadimos nuevos tópicos. 

La función encargada de realizar esta función es **_subirTopico_**, esta es su estructura:
```java
  @PostMapping
    @Transactional
    public ResponseEntity subirTopico(@RequestBody @Valid DatosSubirTopico datos){
        Topico topico = topicoRepository.save(new Topico(datos));
        return ResponseEntity.ok(new DatosRespuestaTopico(
                topico.getId(), topico.getTitulo(),topico.getMensaje(),topico.getFecha(),topico.getStatus(),topico.getAutor(),topico.getCurso()
        ));
    }
```
### GET
Se utiliza para poder leer los datos de la base de datos, utilizando GET podemos observar los datos de la base de datos. 

Se utiliza la funcion **_mostrarTopicos_** para poder observar un listado de 10 tópicos ordenados de forma ascendente por su fecha de creación.
```java
@GetMapping
    public ResponseEntity<Page<DatosListadoTopicos>> mostrarTopicos(@PageableDefault(size=10)Pageable paginacion){
        return  ResponseEntity.ok(topicoRepository.findAllByOrderByFechaAsc(paginacion).map(DatosListadoTopicos::new));
    }
```
Se utiliza la función *_mostrarTopicoEspecifico_* para poder observar un topico a partir del id que proporcione el usuario
```java
@GetMapping("/{id}")
    public ResponseEntity<DatosListadoTopicos> mostrarTopicoEspecifico( @PathVariable Long id){
        Topico topico = topicoRepository.getReferenceById(id);

        return ResponseEntity.ok(new DatosListadoTopicos(
                topico.getTitulo(),topico.getMensaje(),topico.getFecha(),topico.getStatus(),topico.getAutor(),topico.getCurso()
        ));
    }
```
### PUT
Se utiliza para poder editar o actualizar datos ya registrados en la base de datos sin necesidad de crear nuevos registros.

Se utiliza la función **_actualizarTopico_** para poder actualizar un topico proporcionando su id, es importante tomar en cuenta que **solo** podemos editar, el titulo, el mensaje y el estado del tópico.
```java
  @PutMapping
    @Transactional
    public  ResponseEntity<DatosRespuestaTopico> actualizarTopico(@RequestBody @Valid DatosActualizarTopico datos){
        Topico topico = topicoRepository.getReferenceById(datos.id());
        topico.actualizarDatos(datos);
        return ResponseEntity.ok(new DatosRespuestaTopico(
                topico.getId(), topico.getTitulo(),topico.getMensaje(),topico.getFecha(),topico.getStatus(),topico.getAutor(),topico.getCurso()
        ));
    }
```
### DELETE
Se utiliza para eliminar o borrar un registro de la base de datos. 

Se utiliza la función **_eliminarTopico_** que elimina un registro a partir del id que se proporcione.
```java
  @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity eliminarTopico(@PathVariable Long id){
        topicoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
```

## Seguridad

Para la implementación y gestión de la autenticación de usuarios, se utilizan las dependencias **auth0** y **Spring Security**.  

El método de autenticación utilizado es **Bearer token** que consiste en generar un token a partir de recibir la información del usuario (email y contraseña). Si el usuario corresponde a uno registrado en la base de datos, se genera un token que se utiliza en el CRUD para autenticar la acción y poder ejecutarla.

### Generación de Token

Para la creación del token se utiliza la clase **Token Service**:
```java
  @Service
public class TokenService {
    @Value("${forohub.security.secret}")
    private  String foroSecret;

    public String generarToken(Usuario usuario){
        try {
            Algorithm algorithm = Algorithm.HMAC256(foroSecret);
            return JWT.create()
                    .withIssuer("ForoHub")
                    .withSubject(usuario.getEmail())
                    .withClaim("id",usuario.getId())
                    .withExpiresAt(generarFechaExpiracion())
                    .sign(algorithm);
        }catch (JWTCreationException exception){
            throw  new RuntimeException(exception.getMessage());
        }
    }

    private Instant generarFechaExpiracion() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-05:00"));
    }

    public String getSubject(String token) {
        if (token == null){ throw new RuntimeException(); }
        DecodedJWT verifier = null;
        try {
            Algorithm algorithm = Algorithm.HMAC256(foroSecret);
            verifier = JWT.require(algorithm)
                    .withIssuer("ForoHub")
                    .build()
                    .verify(token);
            verifier.getSubject();
        } catch (JWTVerificationException e) {
            throw new RuntimeException(e.getMessage());
        }
        if (verifier.getSubject() == null){
            throw  new RuntimeException("Verifier invalido");
        }
        return verifier.getSubject();
    }
}
```
Como podemos observar se utiliza el algoritmo **HMAC256** para poder encriptar el codigo y así generar el token. Además, se añade un tiempo de expiración de 2hrs para forzar al ususario a iniciar sesión cada 2 hrs.

### Filtro de autenticación

La clase **SecurityFilter** es la encargada de verificar que el usuario exista o se encuentre en la base de datos. Este funcionamiento se implementa en la clase **SecurityConfigurations** que se encarga de implemetar la seguridad a todo el programa exepción de el _login_ que no puede permanecer protegido porque en todo caso no habría manera de generar un token para registrarse. Además, esta clase utiliza BCrypt para poder desencriptar la contraseña.

#### SecurityFilter class
```java
  @Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UsuarioRepository usuarioRepository;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var authHeader = request.getHeader("Authorization");
        if (authHeader != null){
            var token = authHeader.replace("Bearer ","");
            var subject = tokenService.getSubject(token);
            if (subject != null){
                var usuario = usuarioRepository.findByEmail(subject);
                var authentication = new UsernamePasswordAuthenticationToken(usuario, null,usuario.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request,response);
    }
}
```
### SecurityConfigurations class
```java
  @Configuration
@EnableWebSecurity
public class SecurityConfigurations {
    @Autowired
    private  SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws  Exception{
        return  httpSecurity.csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                                .anyRequest().authenticated())
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
        throws  Exception{
        return  authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }
}
```
