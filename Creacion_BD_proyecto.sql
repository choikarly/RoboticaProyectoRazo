-- Crear usuario

create user 'administrador_concursos'@'localhost' identified by '12345';
grant all on concurso_robotica.* to 'administrador_concursos'@'localhost';

-- Crear la base de datos

drop schema if exists concurso_robotica;
create schema concurso_robotica;
use concurso_robotica;

create table categoria(
	id_categoria	int primary key auto_increment,
	nombre			varchar(20) not null
);

create table ciudad(
	id_ciudad 	int primary key auto_increment,
    nombre 		varchar(80) not null
);

create table sede(
	id_sede		int primary key auto_increment,
	nombre		varchar(80) not null,
	fk_ciudad	int not null,
    foreign key (fk_ciudad) references ciudad(id_ciudad) ON DELETE RESTRICT ON UPDATE CASCADE
);

create table escuela(
	id_escuela	int primary key,
    foreign key (id_escuela) references sede(id_sede),
    fk_nivel	int not null,
    foreign key (fk_nivel) references categoria(id_categoria)
);

create table evento(
	id_evento	int primary key auto_increment,
	nombre		varchar(40) not null unique,
	fecha		date,
	fk_sede 	int, 
    constraint uk_fecha_sede unique (fecha, fk_sede),
	foreign key (fk_sede) references sede(id_sede) ON DELETE SET NULL ON UPDATE CASCADE
);

create table categoria_evento (
	fk_evento		int not null,
	fk_categoria	int not null,
	constraint id_categoria_evento primary key (fk_evento, fk_categoria),
	foreign key (fk_evento) references evento(id_evento) ON DELETE RESTRICT ON UPDATE CASCADE,
	foreign key (fk_categoria) references categoria(id_categoria) ON DELETE RESTRICT ON UPDATE CASCADE
);
	
create table usuario(
	id_usuario 		int primary key auto_increment,
	nombre_usuario	varchar(80) not null unique,
	clave 			varchar(225) not null
);

create table administrador(
	id_administrador 	int not null,
    foreign key (id_administrador) references usuario (id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE,
    grado 				int default 0
);

create table docente(
	id_docente 		int primary key,
	foreign key (id_docente) references usuario(id_usuario) ON DELETE RESTRICT ON UPDATE CASCADE,
	nombre				varchar(80) not null,
	fecha_nacimiento 	date not null,
	sexo				enum("H","M") not null,
	fk_escuela 			int not null,
	foreign key (fk_escuela) references escuela(id_escuela) ON DELETE RESTRICT ON UPDATE CASCADE,
	especialidad 	varchar(40) not null
);

create table participante(
	id_participante	int primary key auto_increment,
	nombre				varchar(80) not null,
	fecha_nacimiento 	date not null,
	sexo				enum("H","M") not null,
    fk_escuela 			int not null,
	foreign key (fk_escuela) references escuela(id_escuela) ON DELETE RESTRICT ON UPDATE CASCADE,
	carrera 		varchar (50) not null,
	semestre 		tinyint not null,
	num_control 	int not null,
    constraint uk_escuela_num_control unique (fk_escuela, num_control)
);

create table equipo(
	id_equipo		int primary key auto_increment,
	nombre			varchar(80) not null,
	fk_escuela		int not null,
	foreign key (fk_escuela) references escuela(id_escuela) ON DELETE RESTRICT ON UPDATE CASCADE,
    constraint uk_nombre_escuela unique (nombre, fk_escuela)
);

create table inscripcion_equipo (
	fk_coach		int not null,
	foreign key (fk_coach) references docente(id_docente) ON DELETE RESTRICT ON UPDATE CASCADE,
	fk_equipo		int not null,
	foreign key (fk_equipo) references equipo(id_equipo) ON DELETE CASCADE ON UPDATE CASCADE,
	fk_evento		int not null,
	constraint id_inscripcion_equipo primary key (fk_equipo, fk_evento),
	fk_categoria	int not null,
    constraint uk_inscripcion_completa unique (fk_equipo, fk_evento, fk_categoria),
	foreign key (fk_evento, fk_categoria) references categoria_evento(fk_evento, fk_categoria) ON DELETE RESTRICT ON UPDATE CASCADE
);

create table integrante_inscripcion (
	fk_participante	int not null,
	foreign key (fk_participante) references participante(id_participante) ON DELETE CASCADE ON UPDATE CASCADE,
	fk_evento		int not null,
	constraint id_integrante_inscripcion primary key (fk_participante, fk_evento),
	fk_equipo		int not null,
	foreign key (fk_equipo, fk_evento) references inscripcion_equipo(fk_equipo, fk_evento) ON DELETE CASCADE ON UPDATE CASCADE
);

create table asignacion_juez (
	fk_juez			int not null,
	foreign key (fk_juez) references docente(id_docente) ON DELETE CASCADE ON UPDATE CASCADE,
	fk_evento		int not null,
	constraint id_asignacion_juez primary key (fk_juez, fk_evento),
	fk_categoria	int not null,
    constraint uk_asignacion_completa unique (fk_juez, fk_evento, fk_categoria),
	foreign key (fk_evento, fk_categoria) references categoria_evento(fk_evento, fk_categoria) ON DELETE RESTRICT ON UPDATE CASCADE
);

create table criterios_evaluacion(
	puntos_totales			int default -1,
	fk_equipo				int not null,
	fk_evento				int not null,
	fk_categoria			int not null,
    constraint id_criterios_evaluacion primary key (fk_equipo, fk_evento),
	foreign key (fk_equipo, fk_evento, fk_categoria) references inscripcion_equipo(fk_equipo, fk_evento, fk_categoria) ON DELETE RESTRICT ON UPDATE CASCADE
);

create table criterio_prog(
	fk_equipo					int not null,
	fk_evento					int not null,
	soft_prog					boolean not null,
	uso_func					boolean not null,
	complejidad					boolean not null,
	just_prog					boolean not null,
	conocimiento_estr_func		boolean not null,
	depuracion					boolean not null,
	codigo_modular_efi 			boolean not null,
	documentacion				boolean not null,
	vinculación_acciones		boolean not null,
	sensores					boolean not null,
	vinculo_jostick				boolean not null,
	calibración					boolean not null,
	respuesta_dispositivo		boolean not null,
	documentación_codigo		boolean not null,
	demostración_15min			boolean not null,
	no_inconvenientes			boolean not null,
	demostracion_objetivo		boolean not null,
	explicacion_rutina			boolean not null,
	constraint id_criterio_prog PRIMARY KEY (fk_equipo, fk_evento),
	FOREIGN KEY (fk_equipo, fk_evento) REFERENCES criterios_evaluacion(fk_equipo, fk_evento) ON DELETE CASCADE ON UPDATE CASCADE
);

create table criterio_dis(
	fk_equipo					int not null,
	fk_evento					int not null,
	registro_fechas						boolean not null,
	justificacion_cambios_prototipos	boolean not null,
	ortografia_redacción				boolean not null,
	presentación						boolean not null,
	video_animación						boolean not null,
	diseno_modelado_software			boolean not null,
	analisis_elementos					boolean not null,
	ensamble_prototipo					boolean not null,
	modelo_acorde_robot					boolean not null,
	acorde_simulacion_calculos			boolean not null, 
	restricciones_movimiento			boolean not null,
	constraint id_criterio_disc PRIMARY KEY (fk_equipo, fk_evento),
	FOREIGN KEY (fk_equipo, fk_evento) REFERENCES criterios_evaluacion(fk_equipo, fk_evento) ON DELETE CASCADE ON UPDATE CASCADE
);			

create table criterio_const(
	fk_equipo									int not null,
	fk_evento									int not null,
	prototipo_estetico							boolean not null,
	estructuras_estables						boolean not null,
	uso_sistemas_transmision					boolean not null,
	uso_sensores								boolean not null,
	cableado_adecuado							boolean not null,
	calculo_implementacion_sistema_neumático	boolean not null,
	conocimiento_alcance						boolean not null,
	implementación_marca_vex					boolean not null,
	uso_procesador_cortexm3						boolean not null,
	analisis_Estruc								boolean not null,
	relacion_velocidades						boolean not null,
	tren_engranes								boolean not null,
	centro_gravedad								boolean not null,
	sis_transmicion								boolean not null,
	potencia									boolean not null,
	torque										boolean not null,
	velocidad									boolean not null,
	constraint id_criterio_const PRIMARY KEY (fk_equipo, fk_evento),
	FOREIGN KEY (fk_equipo, fk_evento) REFERENCES criterios_evaluacion(fk_equipo, fk_evento) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Creacion del admin

insert into usuario(nombre_usuario, clave) values ("admin", 1029384756);
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
	out mensaje varchar(50)
)
begin
	if exists (select * from sede where nombre = p_nombre and fk_ciudad = p_fk_ciudad) then
		set mensaje = "Ya existe este registro de sede";
	else
		insert into sede (nombre, fk_ciudad) values (p_nombre, p_fk_ciudad);
        set mensaje = "Se ingreso la sede correctamente";
    end if;
end
// delimiter ;

drop procedure if exists ingresar_escuela;
delimiter //
create procedure ingresar_escuela (
		p_nombre varchar(80),
        p_fk_ciudad int,
        p_fk_nivel int,
	out mensaje varchar(50)
)
begin
	if exists (select * from escuela join sede on id_escuela = id_sede
    where nombre = p_nombre and fk_ciudad = p_fk_ciudad and fk_nivel = p_fk_nivel) then
		set mensaje = "Ya existe este registro de escuela";
	else
		insert into sede (nombre, fk_ciudad) values (p_nombre, p_fk_ciudad);
        insert into escuela (id_escuela, fk_nivel) values (last_insert_id(), p_fk_nivel);
        set mensaje = "Se ingreso la escuela correctamente";
    end if;
end
// delimiter ;

-- Dar de alta escuelas
set @mensaje = '0';
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
    out mensaje varchar(180)
)
begin
	if exists (select * from participante where num_control = p_num_control and fk_escuela = p_escuela) then
		set mensaje = concat("El numero de control ",p_usuario," ya esta registrado");
    else
		insert into participante (nombre, fecha_nacimiento, fk_escuela, sexo, carrera, semestre, num_control)
			values (p_nombre, p_fecha_nacimiento, p_escuela, p_sexo,  p_carrera, p_semestre, p_num_control);
        set mensaje = "Se registro al participante correctamente";
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
    out mensaje varchar(100)
)
begin
	if exists (select * from usuario where nombre_usuario like p_usuario) then
		set mensaje = concat("El usuario ",p_usuario," ya esta registrado");
    else
		insert into usuario (nombre_usuario, clave) values (p_usuario, p_clave);
		insert into docente (id_docente, nombre, fecha_nacimiento, fk_escuela, sexo, especialidad)
			values (last_insert_id(), p_nombre, p_fecha_nacimiento, p_escuela, p_sexo, p_especialidad);
        set mensaje = "Se registro al docente correctamente";
	end if;
end
// delimiter ;

/*
drop procedure sp_inscribir_equipo_completo;
DELIMITER //
CREATE PROCEDURE sp_inscribir_equipo_completo(
    IN p_fk_equipo INT,
    IN p_fk_coach INT,
    IN p_fk_evento INT,
    IN p_fk_categoria INT,
    IN p_id_participante_1 INT,
    IN p_id_participante_2 INT,
    IN p_id_participante_3 INT
)
BEGIN
    INSERT INTO inscripcion_equipo (fk_equipo, fk_coach, fk_evento, fk_categoria) VALUES (p_fk_equipo, p_fk_coach, p_fk_evento, p_fk_categoria);
    INSERT INTO integrante_inscripcion (fk_participante, fk_equipo, fk_evento) VALUES (p_id_participante_1, p_fk_equipo, p_fk_evento);
    INSERT INTO integrante_inscripcion (fk_participante, fk_equipo, fk_evento) VALUES (p_id_participante_2, p_fk_equipo, p_fk_evento);
    INSERT INTO integrante_inscripcion (fk_participante, fk_equipo, fk_evento) VALUES (p_id_participante_3, p_fk_equipo, p_fk_evento);
    COMMIT;
END
// DELIMITER ; */

drop procedure if exists crear_evento;
delimiter //
create procedure crear_evento(
	p_nombre_evento varchar(40),
    p_fecha date,
	p_fk_sede_escuela int,
    out mensaje varchar(50)
)
begin
	if exists (select * from evento where fecha like p_fecha and fk_sede_escuela like p_fk_sede_escuela) then
		set mensaje = "Ya existe un evento en esa sede el mismo dia";
	elseif exists (select * from evento where nombre like p_nombre_evento) then
		set mensaje = concat("Ya existe un evento con el nombre ",p_nombre_evento);
    else
		insert into evento (nombre, fecha, fk_sede_escuela) values (p_nombre_evento, p_fecha, p_fk_sede_escuela);
        set mensaje = "Alta exitosa";
	end if;
end
// delimiter ;

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
	select id_escuela, nombre, ciudad, nivel from sede join ciudad on id_ciudad = id_sede;
end
// delimiter ;

