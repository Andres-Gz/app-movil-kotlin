-- Editar y eliminar movimientos desde la app.
--
-- Se corre una vez en el SQL Editor del proyecto (después de configuracion_cuentas.sql).
-- Se puede volver a correr sin problema.
--
-- Igual que registrar_ingreso y registrar_gasto: la app no escribe directo en las tablas. Cada
-- función toma al usuario de la sesión y solo toca movimientos suyos: si el id es de otra persona,
-- responde que no existe. Eliminar no borra la fila: la marca como inactiva (activo = false), así
-- deja de contar en el balance y en la lista, pero queda el registro.

-- Busca al usuario de la sesión; si no tiene perfil, corta con un error.
create or replace function public.usuario_de_sesion()
returns bigint
language plpgsql
stable
security definer
set search_path = public
as $$
declare
  v_usuario bigint;
begin
  select id_usuario into v_usuario
  from usuarios
  where lower(correo) = lower(auth.jwt() ->> 'email');

  if v_usuario is null then
    raise exception 'No hay un perfil para esta sesión' using errcode = '42501';
  end if;
  return v_usuario;
end;
$$;

-- Busca (o crea) la categoría de gasto del usuario a partir de la clave que manda la app.
create or replace function public.categoria_de_gasto(p_usuario bigint, p_clave text)
returns bigint
language plpgsql
security definer
set search_path = public
as $$
declare
  v_nombre text;
  v_categoria bigint;
begin
  v_nombre := case p_clave
    when 'gastos_fijos' then 'Gastos fijos'
    when 'comida' then 'Comida'
    when 'diversion' then 'Diversión'
    when 'transporte' then 'Transporte'
  end;
  if v_nombre is null then
    raise exception 'Categoría no válida' using errcode = '22023';
  end if;

  select id_categoria into v_categoria
  from categorias
  where id_usuario = p_usuario and tipo = 'gasto' and activo and lower(nombre) = lower(v_nombre)
  order by id_categoria
  limit 1;

  if v_categoria is null then
    insert into categorias (id_usuario, nombre, tipo)
    values (p_usuario, v_nombre, 'gasto')
    returning id_categoria into v_categoria;
  end if;
  return v_categoria;
end;
$$;

-- Cambia el monto y la descripción de un movimiento; si se manda categoría, también la cambia.
create or replace function public.editar_movimiento(
  p_id bigint,
  p_monto numeric,
  p_descripcion text,
  p_categoria text default null
)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
begin
  if p_monto is null or p_monto <= 0 then
    raise exception 'El monto debe ser mayor que cero' using errcode = '22023';
  end if;
  if p_descripcion is null or trim(p_descripcion) = '' then
    raise exception 'La descripción no puede estar vacía' using errcode = '22023';
  end if;

  update movimientos
  set monto = p_monto,
      descripcion = trim(p_descripcion),
      id_categoria = case
        when p_categoria is null then id_categoria
        else categoria_de_gasto(v_usuario, p_categoria)
      end
  where id_movimiento = p_id and id_usuario = v_usuario and activo;

  if not found then
    raise exception 'Movimiento no encontrado' using errcode = 'P0002';
  end if;
end;
$$;

-- Quita un movimiento de la lista y de los totales (lo deja inactivo).
create or replace function public.eliminar_movimiento(p_id bigint)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
begin
  update movimientos
  set activo = false
  where id_movimiento = p_id and id_usuario = v_usuario and activo;

  if not found then
    raise exception 'Movimiento no encontrado' using errcode = 'P0002';
  end if;
end;
$$;

-- Las de ayuda solo las usan las otras funciones; editar y eliminar, quienes iniciaron sesión.
revoke all on function public.usuario_de_sesion() from public, anon, authenticated;
revoke all on function public.categoria_de_gasto(bigint, text) from public, anon, authenticated;
revoke all on function public.editar_movimiento(bigint, numeric, text, text) from public, anon;
revoke all on function public.eliminar_movimiento(bigint) from public, anon;
grant execute on function public.editar_movimiento(bigint, numeric, text, text) to authenticated;
grant execute on function public.eliminar_movimiento(bigint) to authenticated;
