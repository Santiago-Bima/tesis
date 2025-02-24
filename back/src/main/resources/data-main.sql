insert into items (nombre, tipo, unidad_medida) values ('Pategras', 'Queso', 'Unidades') ON CONFLICT (nombre) DO NOTHING;
insert into items (nombre, tipo, unidad_medida) values ('Barra', 'Queso', 'Unidades') ON CONFLICT (nombre) DO NOTHING;
insert into items (nombre, tipo, unidad_medida) values ('Cremoso', 'Queso', 'Unidades') ON CONFLICT (nombre) DO NOTHING;

insert into tipos_quesos (id_tipo_queso, id_queso, dias_maduracion) values (1, 1, 30) ON CONFLICT (id_queso) DO NOTHING;
insert into tipos_quesos (id_tipo_queso, id_queso, dias_maduracion) values (2, 2, 40) ON CONFLICT (id_queso) DO NOTHING;
insert into tipos_quesos (id_tipo_queso, id_queso, dias_maduracion) values (3, 3, 20) ON CONFLICT (id_queso) DO NOTHING;

insert into roles (rol) values ('Subgerente') ON CONFLICT (rol) DO NOTHING;
insert into roles (rol) values ('Operario') ON CONFLICT (rol) DO NOTHING;
insert into roles (rol) values ('Gerente') ON CONFLICT (rol) DO NOTHING;