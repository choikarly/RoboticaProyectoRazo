-- Crear usuario

create user 'administrador_concursos'@'localhost' identified by '12345';
grant all on concurso_robotica.* to 'administrador_concursos'@'localhost';

-- Crear la base de datos

drop schema if exists concurso_robotica;
create schema concurso_robotica;
use concurso_robotica;

create table categoria(
                          id_categoria    int primary key auto_increment,
                          nombre          varchar(20) not null
);

create table ciudad(
                       id_ciudad       int primary key auto_increment,
                       nombre          varchar(80) not null
);

create table sede(
                     id_sede     int primary key auto_increment,
                     nombre      varchar(80) not null,
                     fk_ciudad   int not null,
                     foreign key (fk_ciudad) references ciudad(id_ciudad) ON DELETE RESTRICT ON UPDATE CASCADE
);

create table escuela(
                        id_escuela  int primary key,
                        foreign key (id_escuela) references sede(id_sede),
                        fk_nivel    int not null,
                        foreign key (fk_nivel) references categoria(id_categoria)
);

create table evento(
                       id_evento   int primary key auto_increment,
                       nombre      varchar(40) not null unique,
                       fecha       date,
                       fk_sede     int,
                       constraint uk_fecha_sede unique (fecha, fk_sede),
                       foreign key (fk_sede) references sede(id_sede) ON DELETE SET NULL ON UPDATE CASCADE
);

create table categoria_evento (
                                  fk_evento       int not null,
                                  fk_categoria    int not null,
                                  constraint id_categoria_evento primary key (fk_evento, fk_categoria),
                                  foreign key (fk_evento) references evento(id_evento) ON DELETE RESTRICT ON UPDATE CASCADE,
                                  foreign key (fk_categoria) references categoria(id_categoria) ON DELETE RESTRICT ON UPDATE CASCADE
);

create table usuario(
                        id_usuario      int primary key auto_increment,
                        nombre_usuario  varchar(80) not null unique,
                        clave           varchar(225) not null
);

create table administrador(
                              id_administrador    int not null,
                              foreign key (id_administrador) references usuario (id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE,
                              grado               int default 0
);

create table docente(
                        id_docente      int primary key,
                        foreign key (id_docente) references usuario(id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE,
                        nombre              varchar(80) not null,
                        fecha_nacimiento    date not null,
                        sexo                enum("H","M") not null,
                        fk_escuela          int not null,
                        foreign key (fk_escuela) references escuela(id_escuela) ON DELETE RESTRICT ON UPDATE CASCADE,
                        especialidad    varchar(40) not null
);

create table participante(
                             id_participante int primary key auto_increment,
                             nombre              varchar(80) not null,
                             fecha_nacimiento    date not null,
                             sexo                enum("H","M") not null,
                             fk_escuela          int not null,
                             foreign key (fk_escuela) references escuela(id_escuela) ON DELETE RESTRICT ON UPDATE CASCADE,
                             carrera         varchar (50),
                             semestre        tinyint,
                             num_control     int not null,
                             constraint uk_escuela_num_control unique (fk_escuela, num_control)
);

create table equipo(
                       id_equipo       int primary key auto_increment,
                       nombre          varchar(80) not null,
                       fk_escuela      int not null,
                       foreign key (fk_escuela) references escuela(id_escuela) ON DELETE RESTRICT ON UPDATE CASCADE,
                       constraint uk_nombre_escuela unique (nombre, fk_escuela)
);

create table inscripcion_equipo (
                                    fk_coach        int not null,
                                    foreign key (fk_coach) references docente(id_docente) ON DELETE RESTRICT ON UPDATE CASCADE,
                                    fk_equipo       int not null,
                                    foreign key (fk_equipo) references equipo(id_equipo) ON DELETE CASCADE ON UPDATE CASCADE,
                                    fk_evento       int not null,
                                    constraint id_inscripcion_equipo primary key (fk_equipo, fk_evento),
                                    fk_categoria    int not null,
                                    constraint uk_inscripcion_completa unique (fk_equipo, fk_evento, fk_categoria),
                                    foreign key (fk_evento, fk_categoria) references categoria_evento(fk_evento, fk_categoria) ON DELETE RESTRICT ON UPDATE CASCADE
);

create table integrante_inscripcion (
                                        fk_participante int not null,
                                        foreign key (fk_participante) references participante(id_participante) ON DELETE CASCADE ON UPDATE CASCADE,
                                        fk_evento       int not null,
                                        constraint id_integrante_inscripcion primary key (fk_participante, fk_evento),
                                        fk_equipo       int not null,
                                        foreign key (fk_equipo, fk_evento) references inscripcion_equipo(fk_equipo, fk_evento) ON DELETE CASCADE ON UPDATE CASCADE
);

create table asignacion_juez (
                                 fk_juez         int not null,
                                 foreign key (fk_juez) references docente(id_docente) ON DELETE CASCADE ON UPDATE CASCADE,
                                 fk_evento       int not null,
                                 constraint id_asignacion_juez primary key (fk_juez, fk_evento),
                                 fk_categoria    int not null,
                                 constraint uk_asignacion_completa unique (fk_juez, fk_evento, fk_categoria),
                                 foreign key (fk_evento, fk_categoria) references categoria_evento(fk_evento, fk_categoria) ON DELETE RESTRICT ON UPDATE CASCADE
);

create table criterios_evaluacion(
                                     puntos_totales          int default -1,
                                     fk_equipo               int not null,
                                     fk_evento               int not null,
                                     fk_categoria            int not null,
                                     constraint id_criterios_evaluacion primary key (fk_equipo, fk_evento),
                                     foreign key (fk_equipo, fk_evento, fk_categoria) references inscripcion_equipo(fk_equipo, fk_evento, fk_categoria) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- NOTA: He dejado boolean. Si necesitas puntaje numérico, cambia boolean por INT.
create table criterio_prog(
                              fk_equipo                   int not null,
                              fk_evento                   int not null,
                              soft_prog                   boolean not null,
                              uso_func                    boolean not null,
                              complejidad                 boolean not null,
                              just_prog                   boolean not null,
                              conocimiento_estr_func      boolean not null,
                              depuracion                  boolean not null,
                              codigo_modular_efi          boolean not null,
                              documentacion               boolean not null,
                              vinculación_acciones        boolean not null,
                              sensores                    boolean not null,
                              vinculo_jostick             boolean not null,
                              calibración                 boolean not null,
                              respuesta_dispositivo       boolean not null,
                              documentación_codigo        boolean not null,
                              demostración_15min          boolean not null,
                              no_inconvenientes           boolean not null,
                              demostracion_objetivo       boolean not null,
                              explicacion_rutina          boolean not null,
                              constraint id_criterio_prog PRIMARY KEY (fk_equipo, fk_evento),
                              FOREIGN KEY (fk_equipo, fk_evento) REFERENCES criterios_evaluacion(fk_equipo, fk_evento) ON DELETE CASCADE ON UPDATE CASCADE
);

create table criterio_dis(
                             fk_equipo                   int not null,
                             fk_evento                   int not null,
                             registro_fechas                     boolean not null,
                             justificacion_cambios_prototipos    boolean not null,
                             ortografia_redacción                boolean not null,
                             presentación                        boolean not null,
                             video_animación                     boolean not null,
                             diseno_modelado_software            boolean not null,
                             analisis_elementos                  boolean not null,
                             ensamble_prototipo                  boolean not null,
                             modelo_acorde_robot                 boolean not null,
                             acorde_simulacion_calculos          boolean not null,
                             restricciones_movimiento            boolean not null,
                             constraint id_criterio_disc PRIMARY KEY (fk_equipo, fk_evento),
                             FOREIGN KEY (fk_equipo, fk_evento) REFERENCES criterios_evaluacion(fk_equipo, fk_evento) ON DELETE CASCADE ON UPDATE CASCADE
);

create table criterio_const(
                               fk_equipo                                   int not null,
                               fk_evento                                   int not null,
                               prototipo_estetico                          boolean not null,
                               estructuras_estables                        boolean not null,
                               uso_sistemas_transmision                    boolean not null,
                               uso_sensores                                boolean not null,
                               cableado_adecuado                           boolean not null,
                               calculo_implementacion_sistema_neumático    boolean not null,
                               conocimiento_alcance                        boolean not null,
                               implementación_marca_vex                    boolean not null,
                               uso_procesador_cortexm3                     boolean not null,
                               analisis_Estruc                             boolean not null,
                               relacion_velocidades                        boolean not null,
                               tren_engranes                               boolean not null,
                               centro_gravedad                             boolean not null,
                               sis_transmicion                             boolean not null,
                               potencia                                    boolean not null,
                               torque                                      boolean not null,
                               velocidad                                   boolean not null,
                               constraint id_criterio_const PRIMARY KEY (fk_equipo, fk_evento),
                               FOREIGN KEY (fk_equipo, fk_evento) REFERENCES criterios_evaluacion(fk_equipo, fk_evento) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Creacion del admin

insert into usuario(nombre_usuario, clave) values ("admin", "1029384756");
insert into administrador(id_administrador, grado) values (last_insert_id(), 3);

-- Creacion de categorias

insert into categoria(nombre) values ("Primaria");
insert into categoria(nombre) values ("Secundaria");
insert into categoria(nombre) values ("Bachillerato");
insert into categoria(nombre) values ("Profesional");

-- Dar de alta ciudades

insert into ciudad(nombre) values ("Tampico");
insert into ciudad(nombre) values ("Madero");
insert into ciudad(nombre) values ("Altamira");

-- Procesos almacenados
drop procedure if exists ingresar_sede;
delimiter //
create procedure ingresar_sede (
    p_nombre varchar(80),
    p_fk_ciudad int,
    out aviso tinyint
)
begin
	if exists (select * from sede where nombre = p_nombre and fk_ciudad = p_fk_ciudad) then
		set aviso = -1; -- Sede existente en esa
else
		insert into sede (nombre, fk_ciudad) values (p_nombre, p_fk_ciudad);
        set aviso = 1; -- Se ingreso la sede correctamente
end if;
end
// delimiter ;

drop procedure if exists ingresar_escuela;
delimiter //
create procedure ingresar_escuela (
    p_nombre varchar(80),
    p_fk_ciudad int,
    p_fk_nivel int,
    out aviso tinyint
)
begin
	if exists (select * from escuela join sede on id_escuela = id_sede
    where nombre = p_nombre and fk_ciudad = p_fk_ciudad and fk_nivel = p_fk_nivel) then
		set aviso = -1; -- Sede existente en esa
else
		insert into sede (nombre, fk_ciudad) values (p_nombre, p_fk_ciudad);
insert into escuela (id_escuela, fk_nivel) values (last_insert_id(), p_fk_nivel);
set aviso = 1; -- Se ingreso la sede correctamente
end if;
end
// delimiter ;

-- Dar de alta escuelas
set @mensaje = 0;
CALL ingresar_escuela("Instituto Tecnológico de Ciudad Madero (ITCM)", 2, 4, @mensaje);
CALL ingresar_escuela("Universidad Autonoma de Tamaulipas (UAT)", 1, 4, @mensaje);
CALL ingresar_escuela("Centro de Bachillerato Tecnológico Industrial y de Servicio N.103(CBTis 103)", 2, 3, @mensaje);
CALL ingresar_escuela("Escuela Secundaria N.3 Club de Leones", 1, 2, @mensaje);
CALL ingresar_escuela("Universidad Tecnologica de Altamira (UT Altamira)", 3, 4, @mensaje);
CALL ingresar_escuela("Escuela Primaria Justo Sierra", 1, 1, @mensaje);
CALL ingresar_escuela("Colegio Arboledas A.C", 3, 1, @mensaje);
CALL ingresar_escuela("Centro de Estudios Tecnológico Industrial y de Servicio N.109 (CEBtis 109)", 2, 3, @mensaje);
CALL ingresar_escuela("Escuela Secundaria General N.1 Melchor Ocampo", 2, 2, @mensaje);

drop procedure if exists registrar_competidor;
delimiter //
create procedure registrar_competidor(
    p_nombre varchar(80),
    p_fecha_nacimiento date,
    p_escuela int,
    p_sexo enum("H","M"),
    p_carrera varchar(40),
    p_semestre tinyint,
    p_num_control int,
    out aviso tinyint
        )
begin
    if exists (select * from participante where num_control = p_num_control and fk_escuela = p_escuela) then
        set aviso = -1; -- Ya existe el registro de ese numero de control en esa escuela
else
        insert into participante (nombre, fecha_nacimiento, fk_escuela, sexo, carrera, semestre, num_control)
            values (p_nombre, p_fecha_nacimiento, p_escuela, p_sexo,  p_carrera, p_semestre, p_num_control);
        set aviso = 1; -- Registro exitoso
end if;
end
// delimiter ;

drop procedure if exists registrar_docente;
delimiter //
create procedure registrar_docente(
    p_nombre varchar(80),
    p_usuario varchar(80),
    p_clave varchar(225),
    p_fecha_nacimiento date,
    p_escuela int,
    p_sexo enum("H","M"),
    p_especialidad varchar(40),
    out aviso tinyint
        )
begin
    declare v_edad INT;
    set v_edad = TIMESTAMPDIFF(YEAR, p_fecha_nacimiento, CURDATE());
    if v_edad >= 18 then
        if exists (select * from usuario where nombre_usuario = p_usuario) then
            set aviso = 0;-- Nombre de usuario existente
else
            insert into usuario (nombre_usuario, clave) values (p_usuario, p_clave);
insert into docente (id_docente, nombre, fecha_nacimiento, fk_escuela, sexo, especialidad)
values (last_insert_id(), p_nombre, p_fecha_nacimiento, p_escuela, p_sexo, p_especialidad);
set aviso = 1; -- Se registro correctamente
end if;
else
        set aviso = -1; -- Menor de edad (no puede ser docente)
end if;
end
// delimiter ;

set @mensaje = "0";
call registrar_docente("Jorge Herrera Hipolito", "Herrera220", "1234", "1980-08-21", 1, "H", "Redes de computadoras", @mensaje);
select @mensaje;

drop procedure if exists crear_evento;
delimiter //
create procedure crear_evento(
    p_nombre_evento varchar(40),
    p_fecha date,
    p_fk_sede int,
    out aviso tinyint
)
begin
	if exists (select * from evento where fecha like p_fecha and fk_sede like p_fk_sede) then
		set aviso = -1; -- Ya existe un evento en esa sede ese mismo dia
	elseif exists (select * from evento where nombre like p_nombre_evento) then
		set aviso = 0; -- Ya existe un evento con ese mismo nombre
else
		insert into evento (nombre, fecha, fk_sede) values (p_nombre_evento, p_fecha, p_fk_sede);
        insert into categoria_evento (fk_evento, fk_categoria) select last_insert_id(), id_categoria from categoria;
        set aviso = 1; -- Se creo correctamente el equipo
end if;
end
// delimiter ;

set @aviso = 0;
call crear_evento("OtakuVex","2025-12-12",1, @aviso);

drop function if exists grado_admin;
delimiter //
create function grado_admin(
    f_id_usuario int
) returns int
    reads sql data
begin
	declare v_nivel int;
	if exists (select * from administrador where id_administrador = f_id_usuario) then
select grado into v_nivel from administrador where id_administrador = f_id_usuario;
else
		set v_nivel = -1;
end if;
return v_nivel;
end
// delimiter ;

drop procedure if exists inicio_sesion;
delimiter //
create procedure inicio_sesion(
    p_nombre_usuario	varchar(80),
    p_clave 			varchar(225),
    out p_grado 		tinyint,
    out p_id_usuario 	int
)
begin
	if exists (select * from usuario where nombre_usuario = p_nombre_usuario and clave = p_clave) then
select id_usuario into p_id_usuario from usuario where nombre_usuario = p_nombre_usuario;
select concurso_robotica.grado_admin(p_id_usuario) into p_grado;
else
		set p_grado = -2;
        set p_id_usuario = -1;
end if;
end
// delimiter ;

drop procedure if exists retornar_categorias;
delimiter //
create procedure retornar_categorias()
begin
select * from categoria;
end
// delimiter ;

drop procedure if exists retornar_ciudades;
delimiter //
create procedure retornar_ciudades()
begin
select * from ciudad;
end
// delimiter ;

drop procedure if exists retornar_escuelas;
delimiter //
create procedure retornar_escuelas()
begin
select id_escuela, nombre, fk_ciudad, fk_nivel from sede join escuela on id_escuela = id_sede;
end
// delimiter ;

drop procedure if exists retornar_docentes;
delimiter //
create procedure retornar_docentes()
begin
select distinct id_docente, docente.nombre, sede.nombre as escuela, especialidad from docente
                                                                                          join escuela on id_escuela = fk_escuela
                                                                                          join sede on id_escuela = id_sede;
end
// delimiter ;

drop procedure if exists retornar_coach;
delimiter //
create procedure retornar_coach()
begin
select distinct id_docente, docente.nombre, sede.nombre as escuela, especialidad from docente
                                                                                          join inscripcion_equipo on id_docente = fk_coach
                                                                                          join escuela on id_escuela = fk_escuela
                                                                                          join sede on id_escuela = id_sede;
end
// delimiter ;

drop procedure if exists retornar_juez;
delimiter //
create procedure retornar_juez()
begin
select distinct id_docente, docente.nombre, sede.nombre as escuela, especialidad from docente
                                                                                          join asignacion_juez on id_docente = fk_juez
                                                                                          join escuela on id_escuela = fk_escuela
                                                                                          join sede on id_escuela = id_sede;
end
// delimiter ;

drop procedure if exists retornar_coach_juez;
delimiter //
create procedure retornar_coach_juez()
begin
select distinct id_docente, docente.nombre, sede.nombre as escuela, especialidad from docente
                                                                                          join asignacion_juez on id_docente = fk_juez
                                                                                          join inscripcion_equipo on id_docente = fk_coach
                                                                                          join escuela on id_escuela = fk_escuela
                                                                                          join sede on id_escuela = id_sede;
end
// delimiter ;

drop procedure if exists retornar_eventos;
delimiter //
create procedure retornar_eventos()
begin
select evento.id_evento,evento.nombre, sede.nombre as sede, fecha from evento
                                                                           join sede on fk_sede = id_sede;
end
// delimiter ;




select * from docente;
INSERT INTO categoria_evento (fk_evento, fk_categoria) VALUES (1, 1);
INSERT INTO inscripcion_equipo (fk_coach, fk_equipo, fk_evento, fk_categoria)
VALUES (2, 1, 1, 1);






drop procedure if exists retornar_equipos_coach;
delimiter //
create procedure retornar_equipos_coach(
    p_id_coach int
)
begin
select equipo.nombre as equipo, sede.nombre as escuela, evento.nombre as evento, categoria.nombre as categoria
from inscripcion_equipo
         join equipo on id_equipo = fk_equipo
         join escuela on fk_escuela = id_escuela
         join sede on id_escuela = id_sede
         join evento on fk_evento = id_evento
         join categoria on fk_categoria = id_categoria
where fk_coach = p_id_coach;
end
// delimiter ;

drop procedure if exists crear_equipo;
delimiter //
create procedure crear_equipo(
    p_nombre varchar(80),
    p_fk_escuela int,
    out p_id_equipo int
)
begin
	if exists (select * from equipo where nombre = p_nombre and fk_escuela = p_fk_escuela) then
		set p_id_equipo = (select id_equipo from equipo where nombre = p_nombre and fk_escuela = p_fk_escuela);
else
		insert into equipo (nombre, fk_escuela) values (p_nombre, p_fk_escuela);
        set p_id_equipo = last_insert_id();
end if;
end
// delimiter ;

drop procedure if exists registrar_equipo;
delimiter //
create procedure registrar_equipo(
    p_fk_coach int,
    p_fk_equipo int,
    p_fk_evento int,
    p_fk_categoria int,
    p_fk_participante1 int,
    p_fk_participante2 int,
    p_fk_participante3 int,
    out aviso tinyint
)
begin
	if exists (select * from inscripcion_equipo where fk_equipo = p_fk_equipo and fk_evento = p_fk_evento) then
		set aviso = -1; -- El equipo ya esta registrado en este evento
else
		insert into inscripcion_equipo(fk_coach, fk_equipo, fk_evento, fk_categoria) values (p_fk_coach, p_fk_equipo, p_fk_evento, p_fk_categoria);
insert into integrante_inscripcion(fk_participante, fk_evento, fk_equipo) values (p_fk_participante1, p_fk_evento, p_fk_equipo);
insert into integrante_inscripcion(fk_participante, fk_evento, fk_equipo) values (p_fk_participante2, p_fk_evento, p_fk_equipo);
insert into integrante_inscripcion(fk_participante, fk_evento, fk_equipo) values (p_fk_participante3, p_fk_evento, p_fk_equipo);
set aviso = 1;
end if;
end
// delimiter ;

drop procedure if exists retornar_participante;
delimiter //
create procedure retornar_participante(
    p_num_control int,
    p_fk_escuela int,
    out p_id_participante int,
    out p_nombre varchar(80)
)
begin
	if exists (select * from participante where num_control = p_num_control and fk_escuela = p_fk_escuela) then
select id_participante, nombre into p_id_participante, p_nombre from participante where num_control = p_num_control and fk_escuela = p_fk_escuela;
else
		set p_id_participante = -1;
		set p_nombre = "";
end if;
end
// delimiter ;

DROP PROCEDURE IF EXISTS retornar_eventos_participados;
DELIMITER //
CREATE PROCEDURE retornar_eventos_participados(
    p_id_usuario INT
)
BEGIN
    -- 1. EVENTOS DONDE ES COACH
SELECT DISTINCT
    e.nombre,
    e.fecha,
    s.nombre as sede,
    'COACH' as mi_rol
FROM evento e
         JOIN sede s ON e.fk_sede = s.id_sede
         JOIN inscripcion_equipo ie ON ie.fk_evento = e.id_evento
WHERE ie.fk_coach = p_id_usuario

UNION

-- 2. EVENTOS DONDE ES JUEZ
SELECT DISTINCT
    e.nombre,
    e.fecha,
    s.nombre as sede,
    'JUEZ' as mi_rol
FROM evento e
         JOIN sede s ON e.fk_sede = s.id_sede
         JOIN asignacion_juez aj ON aj.fk_evento = e.id_evento
WHERE aj.fk_juez = p_id_usuario;
END
// DELIMITER ;

-- ********************* NUEVOS PROCESOS ********************************************
DROP PROCEDURE IF EXISTS obtener_id_escuela_docente;
DELIMITER //
CREATE PROCEDURE obtener_id_escuela_docente(
    IN p_id_docente INT,
    OUT p_id_escuela INT
)
BEGIN
SELECT fk_escuela INTO p_id_escuela
FROM docente
WHERE id_docente = p_id_docente;
END
// DELIMITER ;

DROP PROCEDURE IF EXISTS obtener_nombre_escuela_docente;
DELIMITER //
CREATE PROCEDURE obtener_nombre_escuela_docente(
    IN p_id_docente INT,
    OUT p_nombre_escuela VARCHAR(100)
)
BEGIN
SELECT s.nombre INTO p_nombre_escuela
FROM docente d
         JOIN escuela e ON d.fk_escuela = e.id_escuela
    -- CORRECCIÓN: La tabla escuela se une a sede usando id_escuela = id_sede
         JOIN sede s ON e.id_escuela = s.id_sede
WHERE d.id_docente = p_id_docente;
END
// DELIMITER ;

DROP PROCEDURE IF EXISTS retornar_alumnos_por_escuela;
DELIMITER //
CREATE PROCEDURE retornar_alumnos_por_escuela(
    IN p_id_escuela INT
)
BEGIN
SELECT id_participante, nombre
FROM participante
WHERE fk_escuela = p_id_escuela
ORDER BY nombre ASC;
END
// DELIMITER ;
DROP PROCEDURE IF EXISTS obtener_escuela_equipo;
DELIMITER //
CREATE PROCEDURE obtener_escuela_equipo(
    IN p_id_equipo INT,
    OUT p_id_escuela INT
)
BEGIN
SELECT fk_escuela INTO p_id_escuela
FROM equipo
WHERE id_equipo = p_id_equipo;
END
// DELIMITER ;

DROP PROCEDURE IF EXISTS retornar_docentes_con_roles;
DELIMITER //
CREATE PROCEDURE retornar_docentes_con_roles()
BEGIN
SELECT
    d.id_docente,
    d.nombre,
    s.nombre as escuela,
    d.especialidad, -- Agregué especialidad por si la quieres mostrar
    -- Columna calculada: ¿Es Coach?
    (CASE WHEN EXISTS (SELECT 1 FROM inscripcion_equipo WHERE fk_coach = d.id_docente) THEN 1 ELSE 0 END) as es_coach,
    -- Columna calculada: ¿Es Juez?
    (CASE WHEN EXISTS (SELECT 1 FROM asignacion_juez WHERE fk_juez = d.id_docente) THEN 1 ELSE 0 END) as es_juez
FROM docente d
         JOIN escuela e ON d.fk_escuela = e.id_escuela
    -- AQUÍ ESTABA EL ERROR: Usamos e.id_escuela, no e.id_sede
         JOIN sede s ON e.id_escuela = s.id_sede
ORDER BY d.nombre ASC;
END
// DELIMITER ;

DROP PROCEDURE IF EXISTS retornar_equipos_admin_filtro;
DELIMITER //
CREATE PROCEDURE retornar_equipos_admin_filtro(
    IN p_id_evento INT,    -- Si envías -1, trae todos los eventos
    IN p_id_categoria INT  -- Si envías -1, trae todas las categorías
)
BEGIN
SELECT
    e.nombre as equipo,
    s.nombre as escuela,
    ev.nombre as evento,
    c.nombre as categoria
FROM inscripcion_equipo ie
         JOIN equipo e ON ie.fk_equipo = e.id_equipo
         JOIN escuela esc ON e.fk_escuela = esc.id_escuela
         JOIN sede s ON esc.id_escuela = s.id_sede
         JOIN evento ev ON ie.fk_evento = ev.id_evento
         JOIN categoria c ON ie.fk_categoria = c.id_categoria
WHERE
  -- Lógica del Filtro Inteligente:
    (p_id_evento = -1 OR ie.fk_evento = p_id_evento)
  AND
    (p_id_categoria = -1 OR ie.fk_categoria = p_id_categoria)
ORDER BY ev.fecha DESC, e.nombre ASC;
END
// DELIMITER ;
