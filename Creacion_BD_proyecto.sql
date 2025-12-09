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
                             diagramas_imagenes 				 BOOLEAN NOT NULL,
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
-- ==================================================================
-- SECCIÓN DE FUNCIONES Y PROCEDIMIENTOS ALMACENADOS
-- ==================================================================

-- Función: Grado Admin
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

-- Procedimiento: Inicio de Sesión
drop procedure if exists inicio_sesion;
delimiter //
create procedure inicio_sesion(
    p_nombre_usuario    varchar(80),
    p_clave             varchar(225),
    out p_grado         tinyint,
    out p_id_usuario    int,
    out p_nombre_completo varchar(80)
)
begin
    if exists (select * from usuario where nombre_usuario = p_nombre_usuario and clave = p_clave) then
select id_usuario into p_id_usuario from usuario where nombre_usuario = p_nombre_usuario;
select concurso_robotica.grado_admin(p_id_usuario) into p_grado;
select nombre_usuario into p_nombre_completo from usuario where id_usuario = p_id_usuario;

-- Intentar obtener nombre real si es docente
select nombre into @temp_nombre from docente where id_docente = p_id_usuario;
if @temp_nombre is not null then
            set p_nombre_completo = @temp_nombre;
end if;
else
        set p_grado = -2;
        set p_id_usuario = -1;
        set p_nombre_completo = null;
end if;
end
// delimiter ;

-- Procedimiento: Ingresar Sede
drop procedure if exists ingresar_sede;
delimiter //
create procedure ingresar_sede (
    p_nombre varchar(80),
    p_fk_ciudad int,
    out aviso tinyint
)
begin
    -- validamos que no exista esa sede EN ESA MISMA ciudad
    if exists (select * from sede where nombre = p_nombre and fk_ciudad = p_fk_ciudad) then
        set aviso = -1; -- duplicado
else
        insert into sede (nombre, fk_ciudad) values (p_nombre, p_fk_ciudad);
        set aviso = 1;  -- éxito
end if;
end
// delimiter ;

-- Procedimiento: Ingresar Escuela
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
        set aviso = -1;
else
        insert into sede (nombre, fk_ciudad) values (p_nombre, p_fk_ciudad);
insert into escuela (id_escuela, fk_nivel) values (last_insert_id(), p_fk_nivel);
set aviso = 1;
end if;
end
// delimiter ;

-- Procedimiento: Registrar Competidor
drop procedure if exists registrar_competidor;
delimiter //
create procedure registrar_competidor(
    in p_nombre varchar(80),
    in p_fecha_nacimiento date,
    in p_escuela int,
    in p_sexo enum("H","M"),
    in p_carrera varchar(40),
    in p_semestre tinyint,
    in p_num_control int,
    out aviso tinyint
        )
begin
    declare v_nivel int;
    declare v_edad int;
    declare v_edad_minima int;

select fk_nivel into v_nivel from escuela where id_escuela = p_escuela;

-- Definir edad mínima según nivel
case v_nivel
        when 1 then set v_edad_minima = 6;  -- Primaria
when 2 then set v_edad_minima = 12; -- Secundaria
when 3 then set v_edad_minima = 15; -- Bachillerato
when 4 then set v_edad_minima = 17; -- Universidad
else set v_edad_minima = 0;
end case;

    set v_edad = timestampdiff(year, p_fecha_nacimiento, curdate());

    if v_edad < v_edad_minima then
        set aviso = -3; -- Error: Muy joven
    elseif exists (select * from participante where num_control = p_num_control and fk_escuela = p_escuela) then
        set aviso = -1; -- Error: Duplicado
else
        insert into participante (nombre, fecha_nacimiento, fk_escuela, sexo, carrera, semestre, num_control)
        values (p_nombre, p_fecha_nacimiento, p_escuela, p_sexo, p_carrera, p_semestre, p_num_control);
        set aviso = 1;
end if;
end
// delimiter ;

-- Procedimiento: Registrar Docente
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
    declare v_edad int;
    set v_edad = timestampdiff(year, p_fecha_nacimiento, curdate());

    if v_edad >= 18 then
        if exists (select * from usuario where nombre_usuario = p_usuario) then
            set aviso = 0; -- Usuario existente
else
            insert into usuario (nombre_usuario, clave) values (p_usuario, p_clave);
insert into docente (id_docente, nombre, fecha_nacimiento, fk_escuela, sexo, especialidad)
values (last_insert_id(), p_nombre, p_fecha_nacimiento, p_escuela, p_sexo, p_especialidad);
set aviso = 1;
end if;
else
        set aviso = -1; -- Menor de edad
end if;
end
// delimiter ;

-- Procedimiento: Crear Evento
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
        set aviso = -1; -- Evento existente en esa sede y fecha
    elseif exists (select * from evento where nombre like p_nombre_evento) then
        set aviso = 0; -- Nombre duplicado
else
        insert into evento (nombre, fecha, fk_sede) values (p_nombre_evento, p_fecha, p_fk_sede);
insert into categoria_evento (fk_evento, fk_categoria) select last_insert_id(), id_categoria from categoria;
set aviso = 1;
end if;
end
// delimiter ;

-- Procedimiento: Crear Equipo
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

-- Procedimiento: Registrar Equipo (Inscripción)
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
        set aviso = -1; -- Equipo ya registrado en este evento
else
        insert into inscripcion_equipo(fk_coach, fk_equipo, fk_evento, fk_categoria)
        values (p_fk_coach, p_fk_equipo, p_fk_evento, p_fk_categoria);

insert into integrante_inscripcion(fk_participante, fk_evento, fk_equipo) values (p_fk_participante1, p_fk_evento, p_fk_equipo);
insert into integrante_inscripcion(fk_participante, fk_evento, fk_equipo) values (p_fk_participante2, p_fk_evento, p_fk_equipo);
insert into integrante_inscripcion(fk_participante, fk_evento, fk_equipo) values (p_fk_participante3, p_fk_evento, p_fk_equipo);
set aviso = 1;
end if;
end
// delimiter ;

-- Consultas de Retorno (Getters) --

drop procedure if exists retornar_categorias;
delimiter //
create procedure retornar_categorias()
begin
select * from categoria;
end
// delimiter ;

-- 1. procedimiento para llenar el combobox de ciudades
drop procedure if exists retornar_ciudades_combo;
delimiter //
create procedure retornar_ciudades_combo()
begin
select id_ciudad, nombre from ciudad order by nombre asc;
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
select distinct id_docente, docente.nombre, sede.nombre as escuela, especialidad
from docente
         join escuela on id_escuela = fk_escuela
         join sede on id_escuela = id_sede;
end
// delimiter ;

drop procedure if exists retornar_coach;
delimiter //
create procedure retornar_coach()
begin
select distinct id_docente, docente.nombre, sede.nombre as escuela, especialidad
from docente
         join inscripcion_equipo on id_docente = fk_coach
         join escuela on id_escuela = fk_escuela
         join sede on id_escuela = id_sede;
end
// delimiter ;

drop procedure if exists retornar_juez;
delimiter //
create procedure retornar_juez()
begin
select distinct id_docente, docente.nombre, sede.nombre as escuela, especialidad
from docente
         join asignacion_juez on id_docente = fk_juez
         join escuela on id_escuela = fk_escuela
         join sede on id_escuela = id_sede;
end
// delimiter ;

drop procedure if exists retornar_coach_juez;
delimiter //
create procedure retornar_coach_juez()
begin
select distinct id_docente, docente.nombre, sede.nombre as escuela, especialidad
from docente
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
select evento.id_evento, evento.nombre, sede.nombre as sede, fecha
from evento
         join sede on fk_sede = id_sede;
end
// delimiter ;

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
select id_participante, nombre into p_id_participante, p_nombre
from participante where num_control = p_num_control and fk_escuela = p_fk_escuela;
else
        set p_id_participante = -1;
        set p_nombre = "";
end if;
end
// delimiter ;

-- Procedimiento: Retornar Eventos donde participa Usuario (Coach o Juez)
drop procedure if exists retornar_eventos_participados;
delimiter //
create procedure retornar_eventos_participados(
    in p_id_usuario int
)
begin
select
    t.id_evento,
    t.nombre,
    t.fecha,
    t.sede,
    -- lógica para resolver roles múltiples:
    case
        when count(distinct t.mi_rol) > 1 then 'ambos' -- si es coach y juez
        else max(t.mi_rol) -- coach o juez
        end as mi_rol
from (
         -- 1. buscar donde es coach
         select e.id_evento, e.nombre, e.fecha, s.nombre as sede, 'coach' as mi_rol
         from evento e
                  join sede s on e.fk_sede = s.id_sede
                  join inscripcion_equipo ie on ie.fk_evento = e.id_evento
         where ie.fk_coach = p_id_usuario

         union all

         -- 2. buscar donde es juez
         select e.id_evento, e.nombre, e.fecha, s.nombre as sede, 'juez' as mi_rol
         from evento e
                  join sede s on e.fk_sede = s.id_sede
                  join asignacion_juez aj on aj.fk_evento = e.id_evento
         where aj.fk_juez = p_id_usuario
     ) as t
group by t.id_evento, t.nombre, t.fecha, t.sede;
end
//
delimiter ;

-- Procedimiento: Obtener ID Escuela Docente
drop procedure if exists obtener_id_escuela_docente;
delimiter //
create procedure obtener_id_escuela_docente(
    in p_id_docente int,
    out p_id_escuela int
)
begin
select fk_escuela into p_id_escuela from docente where id_docente = p_id_docente;
end
// delimiter ;

-- Procedimiento: Obtener Nombre Escuela Docente
drop procedure if exists obtener_nombre_escuela_docente;
delimiter //
create procedure obtener_nombre_escuela_docente(
    in p_id_docente int,
    out p_nombre_escuela varchar(100)
)
begin
select sede.nombre into p_nombre_escuela
from docente
         join escuela on docente.fk_escuela = escuela.id_escuela
         join sede on escuela.id_escuela = sede.id_sede
where docente.id_docente = p_id_docente;
end
// delimiter ;

-- Procedimiento: Retornar Alumnos por Escuela
drop procedure if exists retornar_alumnos_por_escuela;
delimiter //
create procedure retornar_alumnos_por_escuela(
    in p_id_escuela int
)
begin
select id_participante, nombre
from participante
where fk_escuela = p_id_escuela
order by nombre asc;
end
// delimiter ;

-- Procedimiento: Obtener Escuela de un Equipo
drop procedure if exists obtener_escuela_equipo;
delimiter //
create procedure obtener_escuela_equipo(
    in p_id_equipo int,
    out p_id_escuela int
)
begin
select fk_escuela into p_id_escuela
from equipo
where id_equipo = p_id_equipo;
end
// delimiter ;

-- Procedimiento: Retornar Docentes con Roles (Flags)
drop procedure if exists retornar_docentes_con_roles;
delimiter //
create procedure retornar_docentes_con_roles()
begin
select
    docente.id_docente,
    docente.nombre,
    sede.nombre as escuela,
    docente.especialidad,
    -- Es Coach?
    (case when exists (select 1 from inscripcion_equipo where fk_coach = docente.id_docente) then 1 else 0 end) as es_coach,
    -- Es Juez?
    (case when exists (select 1 from asignacion_juez where fk_juez = docente.id_docente) then 1 else 0 end) as es_juez
from docente
         join escuela on docente.fk_escuela = escuela.id_escuela
         join sede on escuela.id_escuela = sede.id_sede
order by docente.nombre asc;
end
// delimiter ;

-- Procedimiento: Retornar Equipos para Admin con Filtro 
drop procedure if exists retornar_equipos_admin_filtro;
delimiter //
create procedure retornar_equipos_admin_filtro(
    in p_id_evento int,    -- -1 para todos
    in p_id_categoria int  -- -1 para todas
)
begin
select
    equipo.id_equipo,
    evento.id_evento,
    equipo.nombre as equipo,
    sede.nombre as escuela,
    evento.nombre as evento,
    categoria.nombre as categoria
from inscripcion_equipo
         join equipo on inscripcion_equipo.fk_equipo = equipo.id_equipo
         join escuela on equipo.fk_escuela = escuela.id_escuela
         join sede on escuela.id_escuela = sede.id_sede
         join evento on inscripcion_equipo.fk_evento = evento.id_evento
         join categoria on inscripcion_equipo.fk_categoria = categoria.id_categoria
where
    (p_id_evento = -1 or inscripcion_equipo.fk_evento = p_id_evento)
  and
    (p_id_categoria = -1 or inscripcion_equipo.fk_categoria = p_id_categoria)
order by evento.fecha desc, equipo.nombre asc;
end
// delimiter ;

-- Procedimiento: Retornar Equipos de un Docente
drop procedure if exists retornar_equipos_docente;
delimiter //
create procedure retornar_equipos_docente(
    in p_id_docente int
)
begin
select equipo.id_equipo, equipo.nombre
from equipo
         join docente on equipo.fk_escuela = docente.fk_escuela
where docente.id_docente = p_id_docente;
end
// delimiter ;

-- Procedimiento: Obtener Nivel Escuela
drop procedure if exists obtener_nivel_escuela;
delimiter //
create procedure obtener_nivel_escuela(
    in p_id_escuela int,
    out p_nivel int
)
begin
select fk_nivel into p_nivel from escuela where id_escuela = p_id_escuela;
end
// delimiter ;

-- Procedimiento: Retornar Sedes (Combo)
drop procedure if exists retornar_sedes_combo;
delimiter //
create procedure retornar_sedes_combo()
begin
select id_sede, nombre from sede order by nombre asc;
end
// delimiter ;

-- Procedimiento: Retornar Categorias por Evento (Filtra si ya tiene jueces llenos)
drop procedure if exists retornar_categorias_por_evento;
delimiter //
create procedure retornar_categorias_por_evento(
    in p_id_evento int
)
begin
select c.id_categoria, c.nombre
from categoria c
         join categoria_evento ce on c.id_categoria = ce.fk_categoria
where ce.fk_evento = p_id_evento

  -- 1. filtro: menos de 3 jueces
  and (
          select count(*) from asignacion_juez aj
          where aj.fk_evento = p_id_evento and aj.fk_categoria = c.id_categoria
      ) < 3

  -- 2. filtro: que tenga equipos inscritos
  and exists (
    select 1 from inscripcion_equipo ie
    where ie.fk_evento = p_id_evento
      and ie.fk_categoria = c.id_categoria
)
order by c.nombre;
end
// delimiter ;

-- Procedimiento: Obtener Info Detallada Docente
drop procedure if exists obtener_info_docente;
delimiter //
create procedure obtener_info_docente(
    in p_id_docente int
)
begin
select
    docente.nombre,
    usuario.nombre_usuario,
    docente.fecha_nacimiento,
    docente.sexo,
    docente.especialidad,
    sede.nombre as nombre_escuela,
    categoria.nombre as nivel_academico
from docente
         join usuario on docente.id_docente = usuario.id_usuario
         join escuela on docente.fk_escuela = escuela.id_escuela
         join sede on escuela.id_escuela = sede.id_sede
         join categoria on escuela.fk_nivel = categoria.id_categoria
where docente.id_docente = p_id_docente;
end
// delimiter ;

-- Procedimiento: Asignar Terna de Jueces
DROP PROCEDURE IF EXISTS asignar_terna_jueces;
DELIMITER //
CREATE PROCEDURE asignar_terna_jueces(
    IN p_id_evento INT,
    IN p_id_categoria INT,
    IN p_juez1 INT,
    IN p_juez2 INT,
    IN p_juez3 INT,
    OUT aviso INT,                 -- Código de resultado
    OUT p_nombre_conflicto VARCHAR(100) -- Nuevo: Nombre del "culpable"
)
BEGIN
    DECLARE exit handler for sqlexception
BEGIN
ROLLBACK;
SET aviso = -99;
END;

START TRANSACTION;

-- 1. BUSCAR SI HAY UN COACH ENTRE LOS CANDIDATOS
-- Intentamos obtener el nombre del docente que causa el conflicto
SELECT d.nombre INTO p_nombre_conflicto
FROM inscripcion_equipo ie
         JOIN docente d ON ie.fk_coach = d.id_docente
WHERE ie.fk_evento = p_id_evento
  AND ie.fk_categoria = p_id_categoria
  AND ie.fk_coach IN (p_juez1, p_juez2, p_juez3)
    LIMIT 1; -- Solo necesitamos uno para bloquear

-- Si la consulta anterior encontró algo (la variable no es NULL), hay conflicto
IF p_nombre_conflicto IS NOT NULL THEN
        SET aviso = -2; -- Código de Conflicto
ROLLBACK;

-- 2. Validar duplicados (si ya son jueces)
ELSEIF EXISTS (
        SELECT * FROM asignacion_juez
        WHERE fk_evento = p_id_evento
        AND fk_categoria = p_id_categoria
        AND fk_juez IN (p_juez1, p_juez2, p_juez3)
    ) THEN
        SET aviso = 0; -- Código de Duplicado
ROLLBACK;

ELSE
        -- 3. Todo limpio, insertamos
        INSERT INTO asignacion_juez(fk_juez, fk_evento, fk_categoria) VALUES (p_juez1, p_id_evento, p_id_categoria);
INSERT INTO asignacion_juez(fk_juez, fk_evento, fk_categoria) VALUES (p_juez2, p_id_evento, p_id_categoria);
INSERT INTO asignacion_juez(fk_juez, fk_evento, fk_categoria) VALUES (p_juez3, p_id_evento, p_id_categoria);

SET aviso = 1; -- Éxito
COMMIT;
END IF;
END
// DELIMITER ;

-- Procedimiento: Retornar Equipos por Coach
drop procedure if exists retornar_equipos_coach;
delimiter //
create procedure retornar_equipos_coach(
    in p_id_coach int
)
begin
select
    equipo.id_equipo,
    evento.id_evento,
    equipo.nombre as equipo,
    sede.nombre as escuela,
    evento.nombre as evento,
    categoria.nombre as categoria
from inscripcion_equipo
         join equipo on id_equipo = fk_equipo
         join escuela on fk_escuela = id_escuela
         join sede on id_escuela = id_sede
         join evento on fk_evento = id_evento
         join categoria on fk_categoria = id_categoria
where fk_coach = p_id_coach;
end
// delimiter ;

-- Procedimiento: Retornar Miembros de un Equipo en un Evento
drop procedure if exists retornar_miembros_equipo;
delimiter //
create procedure retornar_miembros_equipo(
    in p_id_equipo int,
    in p_id_evento int
)
begin
select participante.nombre, participante.num_control
from participante
         join integrante_inscripcion on participante.id_participante = integrante_inscripcion.fk_participante
where integrante_inscripcion.fk_equipo = p_id_equipo and integrante_inscripcion.fk_evento = p_id_evento;
end
// delimiter ;

-- Procedimiento: Obtener Puntaje Total de un Equipo
drop procedure if exists obtener_puntaje_equipo;
delimiter //
create procedure obtener_puntaje_equipo(
    in p_id_equipo int,
    in p_id_evento int,
    out p_puntos int
)
begin
select puntos_totales into p_puntos
from criterios_evaluacion
where fk_equipo = p_id_equipo and fk_evento = p_id_evento;

-- Si no hay registro, asignamos -2 para indicar que no ha sido evaluado
if p_puntos is null then
        set p_puntos = -2;
end if;
end
// delimiter ;

-- Procedimiento: Retorna los equipos de la categoria que el juez evalua
drop procedure if exists retornar_equipos_a_evaluar;
delimiter //
create procedure retornar_equipos_a_evaluar(
    in p_id_evento int,
    in p_id_juez int
)
begin
select
    e.id_equipo,
    e.nombre as nombre_equipo,
    c.nombre as nombre_categoria
from equipo e
         join inscripcion_equipo ie on ie.fk_equipo = e.id_equipo
         join categoria c on ie.fk_categoria = c.id_categoria
where ie.fk_evento = p_id_evento
  -- aquí el filtro clave: solo equipos de categorías asignadas al juez
  and ie.fk_categoria in (
    select fk_categoria
    from asignacion_juez
    where fk_juez = p_id_juez
      and fk_evento = p_id_evento
)
order by c.nombre, e.nombre;
end
//
delimiter ;

drop procedure if exists retornar_jueces_disponibles;
delimiter //
create procedure retornar_jueces_disponibles(
    in p_id_evento int
)
begin
select id_docente, nombre
from docente
where id_docente not in (
    -- subconsulta: lista negra de jueces ya ocupados en este evento
    select fk_juez
    from asignacion_juez
    where fk_evento = p_id_evento
)
order by nombre asc;
end
// delimiter ;

-- 1. Obtener equipos por evento y categoría
DROP PROCEDURE IF EXISTS retornar_equipos_por_categoria_evento;
DELIMITER //
CREATE PROCEDURE retornar_equipos_por_categoria_evento(
    IN p_id_evento INT
)
BEGIN
    SELECT 
        c.nombre AS categoria,
        e.nombre AS equipo,
        s.nombre AS escuela
    FROM inscripcion_equipo ie
    JOIN equipo e ON ie.fk_equipo = e.id_equipo
    JOIN escuela esc ON e.fk_escuela = esc.id_escuela
    JOIN sede s ON esc.id_escuela = s.id_sede
    JOIN categoria c ON ie.fk_categoria = c.id_categoria
    WHERE ie.fk_evento = p_id_evento
    ORDER BY c.nombre, e.nombre;
END //
DELIMITER ;

-- 2. Obtener jueces por evento y categoría
DROP PROCEDURE IF EXISTS retornar_jueces_por_categoria_evento;
DELIMITER //
CREATE PROCEDURE retornar_jueces_por_categoria_evento(
    IN p_id_evento INT
)
BEGIN
    SELECT 
        c.nombre AS categoria,
        d.nombre AS juez
    FROM asignacion_juez aj
    JOIN docente d ON aj.fk_juez = d.id_docente
    JOIN categoria c ON aj.fk_categoria = c.id_categoria
    WHERE aj.fk_evento = p_id_evento
    ORDER BY c.nombre, d.nombre;
END //
DELIMITER ;

-- PUNTAJE
-- 1. procedimiento para diseño (corregido)
-- Procedimiento: Gestionar Evaluación de Diseño (criterio_dis)
drop procedure if exists gestionar_evaluacion_diseno;
delimiter //
create procedure gestionar_evaluacion_diseno(
    in p_equipo int, in p_evento int,
    in p_reg_fechas tinyint, in p_justif tinyint, in p_ortografia tinyint, in p_presentacion tinyint,
    in p_video tinyint, in p_software tinyint, in p_analisis tinyint, in p_ensamble tinyint,
    in p_modelo tinyint, in p_simulacion tinyint, in p_restricciones tinyint,
    in p_diagramas tinyint -- Nuevo parámetro
)
begin
    declare v_categoria int;
    
    -- 1. Obtener la categoría de la inscripción para crear el registro padre
select fk_categoria into v_categoria
from inscripcion_equipo
where fk_equipo = p_equipo and fk_evento = p_evento limit 1;

-- 2. Asegurar que existe el registro padre en 'criterios_evaluacion'
insert ignore into criterios_evaluacion (fk_equipo, fk_evento, fk_categoria, puntos_totales)
    values (p_equipo, p_evento, v_categoria, 0);

    -- 3. Guardar o Actualizar Diseño (Incluye el nuevo campo diagramas_imagenes)
insert into criterio_dis (fk_equipo, fk_evento, registro_fechas, justificacion_cambios_prototipos, ortografia_redacción, presentación, video_animación, diseno_modelado_software, analisis_elementos, ensamble_prototipo, modelo_acorde_robot, acorde_simulacion_calculos, restricciones_movimiento,
                          diagramas_imagenes)
values (p_equipo, p_evento, p_reg_fechas, p_justif, p_ortografia, p_presentacion, p_video, p_software, p_analisis, p_ensamble, p_modelo, p_simulacion, p_restricciones,
        p_diagramas)
    on duplicate key update
                         registro_fechas=p_reg_fechas, justificacion_cambios_prototipos=p_justif, ortografia_redacción=p_ortografia, presentación=p_presentacion, video_animación=p_video, diseno_modelado_software=p_software, analisis_elementos=p_analisis, ensamble_prototipo=p_ensamble, modelo_acorde_robot=p_modelo, acorde_simulacion_calculos=p_simulacion, restricciones_movimiento=p_restricciones,
                         diagramas_imagenes=p_diagramas;
end
// delimiter ;


drop procedure if exists obtener_evaluacion_diseno;
delimiter //
create procedure obtener_evaluacion_diseno(in p_equipo int, in p_evento int)
begin
select * from criterio_dis where fk_equipo = p_equipo and fk_evento = p_evento;
end
// delimiter ;

-- 2. procedimiento para programación (corregido)
drop procedure if exists gestionar_evaluacion_prog;
delimiter //
create procedure gestionar_evaluacion_prog(
    in p_equipo int, in p_evento int,
    in p_soft tinyint, in p_uso_func tinyint, in p_complejidad tinyint, in p_justif tinyint,
    in p_conocimiento tinyint, in p_depuracion tinyint, in p_modular tinyint, in p_doc tinyint,
    in p_vinc_acc tinyint, in p_sensores tinyint, in p_vinc_joy tinyint, in p_calib tinyint,
    in p_resp tinyint, in p_doc_cod tinyint, in p_demo15 tinyint, in p_no_inc tinyint,
    in p_demo_obj tinyint, in p_explicacion tinyint
)
begin
    declare v_categoria int;

select fk_categoria into v_categoria
from inscripcion_equipo
where fk_equipo = p_equipo and fk_evento = p_evento limit 1;

insert ignore into criterios_evaluacion (fk_equipo, fk_evento, fk_categoria, puntos_totales)
    values (p_equipo, p_evento, v_categoria, 0);

insert into criterio_prog (fk_equipo, fk_evento, soft_prog, uso_func, complejidad, just_prog, conocimiento_estr_func, depuracion, codigo_modular_efi, documentacion, vinculación_acciones, sensores, vinculo_jostick, calibración, respuesta_dispositivo, documentación_codigo, demostración_15min, no_inconvenientes, demostracion_objetivo, explicacion_rutina)
values (p_equipo, p_evento, p_soft, p_uso_func, p_complejidad, p_justif, p_conocimiento, p_depuracion, p_modular, p_doc, p_vinc_acc, p_sensores, p_vinc_joy, p_calib, p_resp, p_doc_cod, p_demo15, p_no_inc, p_demo_obj, p_explicacion)
    on duplicate key update
                         soft_prog=p_soft, uso_func=p_uso_func, complejidad=p_complejidad, just_prog=p_justif, conocimiento_estr_func=p_conocimiento, depuracion=p_depuracion, codigo_modular_efi=p_modular, documentacion=p_doc, vinculación_acciones=p_vinc_acc, sensores=p_sensores, vinculo_jostick=p_vinc_joy, calibración=p_calib, respuesta_dispositivo=p_resp, documentación_codigo=p_doc_cod, demostración_15min=p_demo15, no_inconvenientes=p_no_inc, demostracion_objetivo=p_demo_obj, explicacion_rutina=p_explicacion;

-- actualización del puntaje total
update criterios_evaluacion
set puntos_totales = calcular_puntaje_total(p_equipo, p_evento)
where fk_equipo = p_equipo and fk_evento = p_evento;
end //
delimiter ;

drop procedure if exists obtener_evaluacion_prog;
delimiter //
create procedure obtener_evaluacion_prog(in p_equipo int, in p_evento int)
begin
select * from criterio_prog where fk_equipo = p_equipo and fk_evento = p_evento;
end
// delimiter ;

drop function if exists calcular_puntaje_total;
delimiter //
create function calcular_puntaje_total(p_equipo int, p_evento int)
    returns int
    reads sql data
begin
    declare v_puntos int default 0;
    
    -- variables para sumas crudas y puntajes escalados
    declare v_raw_diseno int default 0;
    declare v_score_diseno int default 0;
    
    declare v_raw_prog int default 0;
    declare v_score_prog int default 0;
    
    declare v_raw_const int default 0;
    declare v_score_const int default 0;

    -- 1. calcular diseño (12 ítems)
select ifnull(
               (registro_fechas + justificacion_cambios_prototipos + ortografia_redacción + presentación +
                video_animación + diseno_modelado_software + analisis_elementos + ensamble_prototipo +
                modelo_acorde_robot + acorde_simulacion_calculos + restricciones_movimiento + diagramas_imagenes), 0)
into v_raw_diseno
from criterio_dis where fk_equipo = p_equipo and fk_evento = p_evento;

-- escalar a 10 puntos: (suma / 12) * 10
-- usamos round para redondear al entero más cercano
set v_score_diseno = round((v_raw_diseno / 12.0) * 10);

    -- 2. calcular programación (18 ítems)
select ifnull(
               (soft_prog + uso_func + complejidad + just_prog + conocimiento_estr_func + depuracion +
                codigo_modular_efi + documentacion + vinculación_acciones + sensores + vinculo_jostick +
                calibración + respuesta_dispositivo + documentación_codigo + demostración_15min +
                no_inconvenientes + demostracion_objetivo + explicacion_rutina), 0)
into v_raw_prog
from criterio_prog where fk_equipo = p_equipo and fk_evento = p_evento;

-- escalar a 10 puntos: (suma / 18) * 10
set v_score_prog = round((v_raw_prog / 18.0) * 10);

    -- 3. calcular construcción (17 ítems)
select ifnull(
               (prototipo_estetico + estructuras_estables + uso_sistemas_transmision + uso_sensores +
                cableado_adecuado + calculo_implementacion_sistema_neumático + conocimiento_alcance +
                implementación_marca_vex + uso_procesador_cortexm3 + analisis_estruc + relacion_velocidades +
                tren_engranes + centro_gravedad + sis_transmicion + potencia + torque + velocidad), 0)
into v_raw_const
from criterio_const where fk_equipo = p_equipo and fk_evento = p_evento;

-- escalar a 10 puntos: (suma / 17) * 10
set v_score_const = round((v_raw_const / 17.0) * 10);

    -- 4. sumar total (máximo 30)
    set v_puntos = v_score_diseno + v_score_prog + v_score_const;

return v_puntos;
end //
delimiter ;

drop procedure if exists gestionar_evaluacion_const;
delimiter //
create procedure gestionar_evaluacion_const(
    in p_equipo int, in p_evento int,
    in p_proto tinyint, in p_estruc tinyint, in p_sis_trans tinyint, in p_sensores tinyint,
    in p_cableado tinyint, in p_neumatico tinyint, in p_alcance tinyint, in p_vex tinyint,
    in p_cortex tinyint, in p_analisis tinyint, in p_rel_vel tinyint, in p_tren tinyint,
    in p_centro tinyint, in p_transmision tinyint, in p_potencia tinyint, in p_torque tinyint, in p_velocidad tinyint
)
begin
    -- 1. insertar/actualizar construcción
insert into criterio_const (fk_equipo, fk_evento, prototipo_estetico, estructuras_estables, uso_sistemas_transmision, uso_sensores, cableado_adecuado, calculo_implementacion_sistema_neumático, conocimiento_alcance, implementación_marca_vex, uso_procesador_cortexm3, analisis_estruc, relacion_velocidades, tren_engranes, centro_gravedad, sis_transmicion, potencia, torque, velocidad)
values (p_equipo, p_evento, p_proto, p_estruc, p_sis_trans, p_sensores, p_cableado, p_neumatico, p_alcance, p_vex, p_cortex, p_analisis, p_rel_vel, p_tren, p_centro, p_transmision, p_potencia, p_torque, p_velocidad)
    on duplicate key update
                         prototipo_estetico=p_proto, estructuras_estables=p_estruc, uso_sistemas_transmision=p_sis_trans, uso_sensores=p_sensores, cableado_adecuado=p_cableado, calculo_implementacion_sistema_neumático=p_neumatico, conocimiento_alcance=p_alcance, implementación_marca_vex=p_vex, uso_procesador_cortexm3=p_cortex, analisis_estruc=p_analisis, relacion_velocidades=p_rel_vel, tren_engranes=p_tren, centro_gravedad=p_centro, sis_transmicion=p_transmision, potencia=p_potencia, torque=p_torque, velocidad=p_velocidad;

-- 2. calcular puntaje real usando la función
update criterios_evaluacion
set puntos_totales = calcular_puntaje_total(p_equipo, p_evento)
where fk_equipo = p_equipo and fk_evento = p_evento;
end //
delimiter ;

drop procedure if exists obtener_evaluacion_const;
delimiter //
create procedure obtener_evaluacion_const(in p_equipo int, in p_evento int)
begin
select * from criterio_const where fk_equipo = p_equipo and fk_evento = p_evento;
end
// delimiter ;

-- ==================================================================
-- SECCIÓN DE INSERCIÓN DE DATOS Y LLAMADAS DE PRUEBA
-- ==================================================================

-- 1. Crear usuario admin
insert into usuario(nombre_usuario, clave) values ("admin", "1029384756");
insert into administrador(id_administrador, grado) values (last_insert_id(), 3);

-- 2. Crear datos estáticos (Categorías y Ciudades)
insert into categoria(nombre) values ("Primaria");
insert into categoria(nombre) values ("Secundaria");
insert into categoria(nombre) values ("Bachillerato");
insert into categoria(nombre) values ("Profesional");

insert into ciudad(nombre) values ("Tampico");
insert into ciudad(nombre) values ("Madero");
insert into ciudad(nombre) values ("Altamira");


set @aviso = 0;

-- 3. Dar de alta Sedes y Escuelas
call ingresar_escuela("Instituto Tecnológico de Ciudad Madero (ITCM)", 2, 4, @aviso);
call ingresar_escuela("Universidad Autonoma de Tamaulipas (UAT)", 1, 4, @aviso);
call ingresar_escuela("Centro de Bachillerato Tecnológico Industrial y de Servicio N.103(CBTis 103)", 2, 3, @aviso);
call ingresar_escuela("Escuela Secundaria N.3 Club de Leones", 1, 2, @aviso);
call ingresar_escuela("Universidad Tecnologica de Altamira (UT Altamira)", 3, 4, @aviso);
call ingresar_escuela("Escuela Primaria Justo Sierra", 1, 1, @aviso);
call ingresar_escuela("Colegio Arboledas A.C", 3, 1, @aviso);
call ingresar_escuela("Centro de Estudios Tecnológico Industrial y de Servicio N.109 (CEBtis 109)", 2, 3, @aviso);
call ingresar_escuela("Escuela Secundaria General N.1 Melchor Ocampo", 2, 2, @aviso);

call ingresar_sede("Espacio Cultural Metropolitano", 1, @aviso);

-- 4. Registrar Docentes (Incluye ejemplos iniciales y los del caso de prueba)
-- Docentes iniciales
call registrar_docente("Jorge Herrera Hipolito", "Herrera220", "1234", "1980-08-21", 2, "H", "Redes de computadoras", @aviso);
call registrar_docente("Mauro", "Mau", "12345678", "2005-06-24", 4, "H", "Calador", @aviso);
call registrar_docente("Carlos Santillan", "Santi", "12345678", "2005-10-15", 4, "H", "Procrasti", @aviso);
call registrar_docente("Jose Esteban", "Tobi", "12345678", "2005-01-13", 4, "H", "Pokemon", @aviso);

-- Docentes adicionales para pruebas
call registrar_docente("Mario Bros", "mario.bros", "nintendo", "1985-09-13", 9, "H", "Mecatrónica", @aviso);
call registrar_docente("Ada Lovelace", "ada.l", "code123", "1980-12-10", 2, "M", "Programación", @aviso);
call registrar_docente("Albert Einstein", "albert.e", "mc2", "1975-03-14", 4, "H", "Física", @aviso);
call registrar_docente("Marie Curie", "marie.c", "radio", "1982-11-07", 5, "M", "Química", @aviso);
CALL registrar_docente("Giancarlo Rossi", "giancarlo.rossi", "password123", "2002-12-30", 6, "H", "Backend", @mensaje);

-- 5. Registrar Competidores
-- Competidores iniciales
call registrar_competidor("Adan", "2005-01-14", 2, "H", "Ing. Sistemas Computacionales", 5, 23070402, @aviso);
call registrar_competidor("Karla", "2005-02-09", 2, "M", "Ing. Sistemas Computacionales", 5, 23070465, @aviso);
call registrar_competidor("Barbara", "2005-12-04", 2, "M", "Ing. Sistemas Computacionales", 5, 23070456, @aviso);

-- Competidores adicionales para pruebas
call registrar_competidor("Peter Parker", "2007-08-10", 9, "H", "Electrónica", 3, 109001, @aviso);
call registrar_competidor("Gwen Stacy", "2007-12-15", 9, "M", "Electrónica", 3, 109002, @aviso);
call registrar_competidor("Miles Morales", "2008-01-20", 9, "H", "Electrónica", 3, 109003, @aviso);
call registrar_competidor("Tony Stark", "2003-05-29", 2, "H", "Ing. Mecatrónica", 7, 2007100, @aviso);
call registrar_competidor("Bruce Banner", "2002-12-18", 2, "H", "Ing. Sistemas", 8, 2007101, @aviso);
call registrar_competidor("Natasha Romanoff", "2004-11-22", 2, "M", "Ing. Industrial", 6, 2007102, @aviso);
call registrar_competidor("Ash Ketchum", "2010-05-20", 4, "H", "N/A", 2, 4001, @aviso);
call registrar_competidor("Misty Waterflower", "2010-09-15", 4, "M", "N/A", 2, 4002, @aviso);
call registrar_competidor("Brock Harrison", "2009-02-10", 4, "H", "N/A", 3, 4003, @aviso);
CALL registrar_competidor("Sofia Hernandez", "2019-11-01", 6, "M", "N/A", 1, 6001, @aviso);
CALL registrar_competidor("Luis Garcia", "2017-08-15", 6, "H", "N/A", 3, 6002, @aviso);
CALL registrar_competidor("Elena Martinez", "2018-05-22", 6, "M", "N/A", 2, 6003, @aviso);

-- 6. Crear Eventos
call crear_evento("OtakuVex","2025-12-12", 1, @aviso);
call crear_evento("Torneo de Robots Madero 2025", "2025-10-20", 2, @aviso);
call crear_evento("Expo Tech Altamira", "2025-11-15", 6, @aviso);

-- 7. Crear Equipos
call crear_equipo("Los Eevees", 2, @id_equipo);
call crear_equipo("Arañas Tecnológicas", 9, @id_equipo);
call crear_equipo("Vengadores ITCM", 2, @id_equipo);
call crear_equipo("Entrenadores Pokémon", 4, @id_equipo);
CALL crear_equipo("Las Tralaleritas", 6, @id_equipo);

-- 8. Registrar Equipos en Eventos (Inscripciones)
-- Los Eevees
call registrar_equipo(2, 1, 1, 4, 1, 2, 3, @aviso);

-- Arañas Tecnológicas
-- (Coach ID 3, Equipo ID 2, Evento ID 2, Categ 3, Partic 4,5,6)
call registrar_equipo(3, 2, 2, 3, 4, 5, 6, @aviso);

-- Vengadores ITCM
-- (Coach ID 4, Equipo ID 3, Evento ID 2, Categ 4, Partic 7,8,9)
call registrar_equipo(4, 3, 2, 4, 7, 8, 9, @aviso);

-- Entrenadores Pokémon
-- (Coach ID 5, Equipo ID 4, Evento ID 3, Categ 2, Partic 10,11,12)
call registrar_equipo(5, 4, 3, 2, 10, 11, 12, @aviso);

CALL registrar_equipo(10, 5, 1, 1, 13, 14, 15, @aviso);

-- ASIGNACIÓN DE JUECES (Usando docentes que son Coaches en otros torneos)
-- Evento: 1 (OtakuVex)
-- Categoría: 1 (Primaria)
-- Jueces: 3, 4, 5 (Mauro, Carlos y José)
CALL asignar_terna_jueces(1, 1, 3, 4, 5, @aviso);