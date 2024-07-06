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
