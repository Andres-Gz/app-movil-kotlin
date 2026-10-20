-- Metas de ahorro: datos extra de cada meta y las funciones de las pantallas de metas.
--
-- Se corre una vez en el SQL Editor del proyecto (después de configuracion_cuentas.sql).
-- Se puede volver a correr sin problema.
--
-- 1. Agrega a `metas` lo que muestra el diseño y la tabla no tenía: la categoría ("Viajes & Ocio"),
--    el icono (un emoji) y la regla de aporte (cuánto y cada cuánto). Son opcionales, así que las
--    metas que ya existen siguen igual.
-- 2. Crea las funciones que usa la app. Igual que con los movimientos, la app no escribe directo en
--    las tablas: cada función toma al usuario de la sesión y solo toca metas suyas (si el id es de
--    otra persona, responde que no existe). Las tablas siguen cerradas por RLS.
--    - resumen_metas: las metas activas, lo ahorrado en cada una y las fechas de los aportes del último año.
--    - crear_meta y editar_meta: guardan nombre, categoría, monto, fecha límite y regla de aporte.
--    - aportar_meta: guarda un aporte en `aportes`, desde la cuenta principal (la crea si no existe).
--    - ajustar_regla_meta: cambia cuánto y cada cuánto se aporta.
--    - eliminar_meta: la quita de la lista (activo = false); sus aportes quedan guardados.

alter table public.metas add column if not exists categoria character varying;
alter table public.metas add column if not exists icono character varying;
alter table public.metas add column if not exists monto_aporte numeric;
-- 'quincenal' o 'mensual'.
alter table public.metas add column if not exists frecuencia_aporte character varying;

-- Busca al usuario de la sesión; si no tiene perfil, corta con un error.
-- (Es la misma de editar_movimientos.sql: se repite para que este archivo se pueda correr solo).
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

-- Revisa la regla de aporte: un monto mayor que cero cada quincena o cada mes.
create or replace function public.revisar_regla_meta(p_frecuencia text, p_monto_aporte numeric)
returns void
language plpgsql
immutable
as $$
begin
  if p_monto_aporte is null or p_monto_aporte <= 0 then
    raise exception 'El aporte debe ser mayor que cero' using errcode = '22023';
  end if;
  if p_frecuencia is null or p_frecuencia not in ('quincenal', 'mensual') then
    raise exception 'Frecuencia no válida' using errcode = '22023';
  end if;
end;
$$;

-- Revisa los datos de una meta nueva o editada y devuelve el nombre y el emoji de su categoría
-- (la app manda la clave: 'viajes', 'emergencia'…).
create or replace function public.revisar_meta(
  p_nombre text,
  p_categoria text,
  p_monto numeric,
  p_fecha_limite date,
  p_frecuencia text,
  p_monto_aporte numeric,
  out categoria text,
  out icono text
)
language plpgsql
stable
as $$
begin
  if p_nombre is null or btrim(p_nombre) = '' then
    raise exception 'La meta necesita un nombre' using errcode = '22023';
  end if;
  if p_monto is null or p_monto <= 0 then
    raise exception 'El monto debe ser mayor que cero' using errcode = '22023';
  end if;
  if p_fecha_limite is null or p_fecha_limite <= current_date then
    raise exception 'La fecha límite debe ser futura' using errcode = '22023';
  end if;
  perform revisar_regla_meta(p_frecuencia, p_monto_aporte);

  select c.nombre, c.icono into categoria, icono
  from (values
    ('viajes', 'Viajes & Ocio', '🏝️'),
    ('emergencia', 'Emergencia', '🛟'),
    ('tecnologia', 'Tecnología', '💻'),
    ('vehiculo', 'Vehículo', '🏍️'),
    ('vivienda', 'Vivienda', '🏠'),
    ('educacion', 'Educación', '🎓')
  ) as c (clave, nombre, icono)
  where c.clave = p_categoria;

  if categoria is null then
    raise exception 'Categoría no válida' using errcode = '22023';
  end if;
end;
$$;

-- Todo lo que muestra la pantalla de metas, en una sola llamada.
create or replace function public.resumen_metas()
returns json
language plpgsql
stable
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
begin
  return json_build_object(
    'metas', coalesce((
      select json_agg(meta order by meta.fecha_limite nulls last, meta.id_meta)
      from (
        select me.id_meta, me.nombre, me.monto_objetivo as objetivo,
               coalesce((select sum(a.monto) from aportes a where a.id_meta = me.id_meta), 0) as ahorrado,
               me.fecha_limite, me.categoria, me.icono, me.monto_aporte, lower(me.frecuencia_aporte) as frecuencia_aporte
        from metas me
        where me.id_usuario = v_usuario and me.activo
      ) as meta
    ), '[]'::json),
    -- Para contar cuántas quincenas seguidas ha aportado.
    'fechas_aportes', coalesce((
      select json_agg(a.fecha order by a.fecha desc)
      from aportes a
      join metas me on me.id_meta = a.id_meta
      where me.id_usuario = v_usuario and me.activo and a.fecha > now() - interval '1 year'
    ), '[]'::json)
  );
end;
$$;

-- Guarda una meta nueva. La fecha límite se guarda al mediodía UTC para que en cualquier zona horaria
-- de América siga siendo el mismo día.
create or replace function public.crear_meta(
  p_nombre text,
  p_categoria text,
  p_monto numeric,
  p_fecha_limite date,
  p_frecuencia text,
  p_monto_aporte numeric
)
returns bigint
language plpgsql
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
  v_datos record;
  v_meta bigint;
begin
  select * into v_datos from revisar_meta(p_nombre, p_categoria, p_monto, p_fecha_limite, p_frecuencia, p_monto_aporte);

  insert into metas (id_usuario, nombre, monto_objetivo, fecha_limite, categoria, icono, monto_aporte, frecuencia_aporte)
  values (
    v_usuario, btrim(p_nombre), p_monto, (p_fecha_limite + time '12:00') at time zone 'UTC',
    v_datos.categoria, v_datos.icono, p_monto_aporte, p_frecuencia
  )
  returning id_meta into v_meta;

  return v_meta;
end;
$$;

-- Cambia los datos de una meta (los mismos que pide crear_meta).
create or replace function public.editar_meta(
  p_id bigint,
  p_nombre text,
  p_categoria text,
  p_monto numeric,
  p_fecha_limite date,
  p_frecuencia text,
  p_monto_aporte numeric
)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
  v_datos record;
begin
  select * into v_datos from revisar_meta(p_nombre, p_categoria, p_monto, p_fecha_limite, p_frecuencia, p_monto_aporte);

  update metas
  set nombre = btrim(p_nombre),
      monto_objetivo = p_monto,
      fecha_limite = (p_fecha_limite + time '12:00') at time zone 'UTC',
      categoria = v_datos.categoria,
      icono = v_datos.icono,
      monto_aporte = p_monto_aporte,
      frecuencia_aporte = p_frecuencia
  where id_meta = p_id and id_usuario = v_usuario and activo;

  if not found then
    raise exception 'Meta no encontrada' using errcode = 'P0002';
  end if;
end;
$$;

-- Guarda un aporte a una meta, desde la cuenta principal del usuario.
create or replace function public.aportar_meta(p_id bigint, p_monto numeric)
returns bigint
language plpgsql
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
  v_cuenta bigint;
  v_aporte bigint;
begin
  if p_monto is null or p_monto <= 0 then
    raise exception 'El aporte debe ser mayor que cero' using errcode = '22023';
  end if;
  if not exists (select 1 from metas where id_meta = p_id and id_usuario = v_usuario and activo) then
    raise exception 'Meta no encontrada' using errcode = 'P0002';
  end if;

  select id_cuenta into v_cuenta
  from cuentas
  where id_usuario = v_usuario and activo
  order by id_cuenta
  limit 1;

  if v_cuenta is null then
    insert into cuentas (id_usuario, nombre, tipo)
    values (v_usuario, 'Cuenta principal', 'efectivo')
    returning id_cuenta into v_cuenta;
  end if;

  insert into aportes (id_meta, id_cuenta, monto)
  values (p_id, v_cuenta, p_monto)
  returning id_aporte into v_aporte;

  return v_aporte;
end;
$$;

-- Cambia cuánto y cada cuánto se aporta a una meta.
create or replace function public.ajustar_regla_meta(p_id bigint, p_monto_aporte numeric, p_frecuencia text)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
begin
  perform revisar_regla_meta(p_frecuencia, p_monto_aporte);

  update metas
  set monto_aporte = p_monto_aporte,
      frecuencia_aporte = p_frecuencia
  where id_meta = p_id and id_usuario = v_usuario and activo;

  if not found then
    raise exception 'Meta no encontrada' using errcode = 'P0002';
  end if;
end;
$$;

-- Quita una meta de la lista (la deja inactiva). Sus aportes no se borran.
create or replace function public.eliminar_meta(p_id bigint)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
begin
  update metas
  set activo = false
  where id_meta = p_id and id_usuario = v_usuario and activo;

  if not found then
    raise exception 'Meta no encontrada' using errcode = 'P0002';
  end if;
end;
$$;

-- Las de ayuda solo las usan las otras funciones; las demás, quienes iniciaron sesión.
revoke all on function public.usuario_de_sesion() from public, anon, authenticated;
revoke all on function public.revisar_regla_meta(text, numeric) from public, anon, authenticated;
revoke all on function public.revisar_meta(text, text, numeric, date, text, numeric) from public, anon, authenticated;
revoke all on function public.resumen_metas() from public, anon;
revoke all on function public.crear_meta(text, text, numeric, date, text, numeric) from public, anon;
revoke all on function public.editar_meta(bigint, text, text, numeric, date, text, numeric) from public, anon;
revoke all on function public.aportar_meta(bigint, numeric) from public, anon;
revoke all on function public.ajustar_regla_meta(bigint, numeric, text) from public, anon;
revoke all on function public.eliminar_meta(bigint) from public, anon;
grant execute on function public.resumen_metas() to authenticated;
grant execute on function public.crear_meta(text, text, numeric, date, text, numeric) to authenticated;
grant execute on function public.editar_meta(bigint, text, text, numeric, date, text, numeric) to authenticated;
grant execute on function public.aportar_meta(bigint, numeric) to authenticated;
grant execute on function public.ajustar_regla_meta(bigint, numeric, text) to authenticated;
grant execute on function public.eliminar_meta(bigint) to authenticated;
