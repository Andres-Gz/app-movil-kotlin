-- El asistente: el chat con Finanzia IA.
--
-- Se corre una vez en el SQL Editor del proyecto, después de configuracion_cuentas.sql, ingresos.sql,
-- gastos.sql, inicio.sql y metas.sql (confirmar una acción usa sus funciones). Se puede volver a
-- correr sin problema.
--
-- Las tablas `conversaciones` y `mensajes` ya existen (esquema.sql). Este archivo:
--   1. Agrega a `mensajes` la acción que propone la IA (registrar un gasto, un ingreso o un aporte a
--      una meta) y su estado. La IA nunca guarda nada sola: propone, la app muestra la propuesta con
--      los botones Confirmar y Cancelar, y solo al confirmar se ejecuta.
--   2. Crea las funciones del chat:
--      - conversaciones_asistente: las conversaciones del usuario, la más reciente primero.
--      - mensajes_asistente: los mensajes de una conversación (la Edge Function los usa como memoria).
--      - guardar_intercambio_asistente: guarda la pregunta y la respuesta; crea la conversación si es nueva.
--      - eliminar_conversacion_asistente: la quita de la lista (activo = false).
--      - confirmar_accion_asistente / cancelar_accion_asistente: resuelven la acción propuesta.
--      - movimientos_asistente: los movimientos entre dos fechas, con totales; la consulta la IA.
-- Todas trabajan solo con el usuario de la sesión: si un id es de otra persona, responden que no existe.


-- 1. La acción propuesta ---------------------------------------------------------------------------

-- Lo que la IA propone guardar, por ejemplo {"tipo": "gasto", "monto": 20000, "categoria": "comida",
-- "descripcion": "Almuerzo"}. Vacío si el mensaje no trae acción.
alter table public.mensajes add column if not exists accion jsonb;
-- 'pendiente', 'confirmada' o 'cancelada'; vacío si el mensaje no trae acción.
alter table public.mensajes add column if not exists estado_accion character varying;

-- Las conversaciones se buscan por usuario y los mensajes por conversación, en orden.
create index if not exists conversaciones_id_usuario_idx on public.conversaciones (id_usuario);
create index if not exists mensajes_id_conversacion_idx on public.mensajes (id_conversacion, id_mensaje);


-- 2. Funciones de ayuda (solo las usan las demás) --------------------------------------------------

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

-- Corta con un error si la conversación no es del usuario o ya se eliminó.
create or replace function public.revisar_conversacion(p_conversacion bigint, p_usuario bigint)
returns void
language plpgsql
stable
security definer
set search_path = public
as $$
begin
  if not exists (
    select 1 from conversaciones
    where id_conversacion = p_conversacion and id_usuario = p_usuario and activo
  ) then
    raise exception 'Conversación no encontrada' using errcode = 'P0002';
  end if;
end;
$$;

-- Un mensaje como lo recibe la app y la Edge Function.
create or replace function public.json_mensaje(p_mensaje public.mensajes)
returns json
language sql
immutable
as $$
  select json_build_object(
    'id_mensaje', p_mensaje.id_mensaje,
    'rol', p_mensaje.rol,
    'contenido', p_mensaje.contenido,
    'fecha', p_mensaje.fecha,
    'accion', p_mensaje.accion,
    'estado_accion', p_mensaje.estado_accion
  );
$$;

-- Toma (y bloquea, para que no se confirme dos veces) un mensaje del usuario con una acción pendiente.
create or replace function public.accion_pendiente(p_mensaje bigint, p_usuario bigint)
returns public.mensajes
language plpgsql
security definer
set search_path = public
as $$
declare
  v_mensaje mensajes;
begin
  select m.* into v_mensaje
  from mensajes m
  join conversaciones c on c.id_conversacion = m.id_conversacion
  where m.id_mensaje = p_mensaje and c.id_usuario = p_usuario and c.activo and m.activo
  for update of m;

  if v_mensaje.id_mensaje is null then
    raise exception 'Mensaje no encontrado' using errcode = 'P0002';
  end if;
  if v_mensaje.accion is null or v_mensaje.estado_accion is distinct from 'pendiente' then
    raise exception 'Esta acción ya se resolvió' using errcode = '22023';
  end if;
  return v_mensaje;
end;
$$;


-- 3. El chat ---------------------------------------------------------------------------------------

create or replace function public.conversaciones_asistente()
returns json
language plpgsql
stable
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
begin
  return coalesce((
    select json_agg(conversacion order by conversacion.fecha_ultimo desc, conversacion.id_conversacion desc)
    from (
      select c.id_conversacion, c.titulo, c.fecha_creacion,
             coalesce(ultimo.fecha, c.fecha_creacion) as fecha_ultimo,
             left(ultimo.contenido, 120) as ultimo_mensaje
      from conversaciones c
      left join lateral (
        select m.fecha, m.contenido
        from mensajes m
        where m.id_conversacion = c.id_conversacion and m.activo
        order by m.id_mensaje desc
        limit 1
      ) as ultimo on true
      where c.id_usuario = v_usuario and c.activo
    ) as conversacion
  ), '[]'::json);
end;
$$;

create or replace function public.mensajes_asistente(p_conversacion bigint)
returns json
language plpgsql
stable
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
begin
  perform revisar_conversacion(p_conversacion, v_usuario);
  return coalesce((
    select json_agg(json_mensaje(m) order by m.id_mensaje)
    from mensajes m
    where m.id_conversacion = p_conversacion and m.activo
  ), '[]'::json);
end;
$$;

-- La pregunta y la respuesta se guardan juntas, cuando la IA ya respondió: si la IA falla no queda
-- una pregunta sin respuesta. Sin conversación (p_conversacion vacío) crea una, con la pregunta como título.
create or replace function public.guardar_intercambio_asistente(
  p_conversacion bigint,
  p_pregunta text,
  p_respuesta text,
  p_accion jsonb
)
returns json
language plpgsql
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
  v_conversacion bigint := p_conversacion;
  v_pregunta mensajes;
  v_respuesta mensajes;
begin
  if nullif(trim(p_pregunta), '') is null or nullif(trim(p_respuesta), '') is null then
    raise exception 'La pregunta y la respuesta no pueden estar vacías' using errcode = '22023';
  end if;
  if p_accion is not null and coalesce(p_accion ->> 'tipo', '') not in ('gasto', 'ingreso', 'aporte') then
    raise exception 'Acción no válida' using errcode = '22023';
  end if;

  if v_conversacion is null then
    insert into conversaciones (id_usuario, titulo)
    values (v_usuario, left(trim(p_pregunta), 60))
    returning id_conversacion into v_conversacion;
  else
    perform revisar_conversacion(v_conversacion, v_usuario);
  end if;

  insert into mensajes (id_conversacion, rol, contenido)
  values (v_conversacion, 'user', trim(p_pregunta))
  returning * into v_pregunta;

  -- clock_timestamp: la hora real de la respuesta (now() es la del inicio de la transacción).
  insert into mensajes (id_conversacion, rol, contenido, fecha, accion, estado_accion)
  values (
    v_conversacion, 'assistant', trim(p_respuesta), clock_timestamp(), p_accion,
    case when p_accion is null then null else 'pendiente' end
  )
  returning * into v_respuesta;

  return json_build_object(
    'id_conversacion', v_conversacion,
    'pregunta', json_mensaje(v_pregunta),
    'respuesta', json_mensaje(v_respuesta)
  );
end;
$$;

create or replace function public.eliminar_conversacion_asistente(p_id bigint)
returns void
language plpgsql
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
begin
  update conversaciones
  set activo = false
  where id_conversacion = p_id and id_usuario = v_usuario and activo;

  if not found then
    raise exception 'Conversación no encontrada' using errcode = 'P0002';
  end if;
end;
$$;

-- Ejecuta lo que propuso la IA con las mismas funciones que usa la app (registrar_gasto,
-- registrar_ingreso y aportar_meta), así valen las mismas reglas. Devuelve el mensaje actualizado.
create or replace function public.confirmar_accion_asistente(p_mensaje bigint)
returns json
language plpgsql
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
  v_mensaje mensajes := accion_pendiente(p_mensaje, v_usuario);
  v_accion jsonb := v_mensaje.accion;
  v_movimiento bigint;
begin
  case v_accion ->> 'tipo'
    when 'gasto' then
      v_movimiento := registrar_gasto((v_accion ->> 'monto')::numeric, v_accion ->> 'categoria');
    when 'ingreso' then
      v_movimiento := registrar_ingreso((v_accion ->> 'monto')::numeric, v_accion ->> 'frecuencia');
    when 'aporte' then
      perform aportar_meta((v_accion ->> 'id_meta')::bigint, (v_accion ->> 'monto')::numeric);
    else
      raise exception 'Acción no válida' using errcode = '22023';
  end case;

  -- registrar_gasto y registrar_ingreso ponen una descripción genérica ("Comida", "Ingreso mensual");
  -- si la IA trajo una más precisa ("Almuerzo"), se usa esa.
  if v_movimiento is not null and nullif(trim(v_accion ->> 'descripcion'), '') is not null then
    update movimientos
    set descripcion = left(trim(v_accion ->> 'descripcion'), 120)
    where id_movimiento = v_movimiento;
  end if;

  update mensajes set estado_accion = 'confirmada' where id_mensaje = p_mensaje returning * into v_mensaje;
  return json_mensaje(v_mensaje);
end;
$$;

create or replace function public.cancelar_accion_asistente(p_mensaje bigint)
returns json
language plpgsql
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
  v_mensaje mensajes := accion_pendiente(p_mensaje, v_usuario);
begin
  update mensajes set estado_accion = 'cancelada' where id_mensaje = v_mensaje.id_mensaje returning * into v_mensaje;
  return json_mensaje(v_mensaje);
end;
$$;


-- 4. Lo que consulta la IA -------------------------------------------------------------------------

-- Los movimientos entre dos fechas (incluidas, en hora de Colombia), con los totales y los gastos por
-- categoría. Lista hasta 100 movimientos, los más recientes; los totales cuentan todos.
-- El resumen general y las metas los lee con resumen_inicio (inicio.sql) y resumen_metas (metas.sql).
create or replace function public.movimientos_asistente(p_desde date, p_hasta date)
returns json
language plpgsql
stable
security definer
set search_path = public
as $$
declare
  v_usuario bigint := usuario_de_sesion();
begin
  if p_desde is null or p_hasta is null or p_desde > p_hasta then
    raise exception 'Rango de fechas no válido' using errcode = '22023';
  end if;

  return (
    with del_periodo as (
      select m.id_movimiento, lower(m.tipo) as tipo, m.monto, m.descripcion,
             m.fecha at time zone 'America/Bogota' as fecha, c.nombre as categoria
      from movimientos m
      left join categorias c on c.id_categoria = m.id_categoria
      where m.id_usuario = v_usuario and m.activo
        and (m.fecha at time zone 'America/Bogota')::date between p_desde and p_hasta
    )
    select json_build_object(
      'desde', p_desde,
      'hasta', p_hasta,
      'cantidad', (select count(*) from del_periodo),
      'total_ingresos', coalesce((select sum(monto) from del_periodo where tipo = 'ingreso'), 0),
      'total_gastos', coalesce((select sum(monto) from del_periodo where tipo = 'gasto'), 0),
      'gastos_por_categoria', coalesce((
        select json_agg(grupo order by grupo.total desc)
        from (
          select coalesce(categoria, 'Sin categoría') as categoria, sum(monto) as total, count(*) as cantidad
          from del_periodo
          where tipo = 'gasto'
          group by 1
        ) as grupo
      ), '[]'::json),
      'movimientos', coalesce((
        select json_agg(ultimo order by ultimo.fecha desc, ultimo.id_movimiento desc)
        from (select * from del_periodo order by fecha desc, id_movimiento desc limit 100) as ultimo
      ), '[]'::json)
    )
  );
end;
$$;


-- 5. Permisos --------------------------------------------------------------------------------------

-- Las de ayuda solo las usan las otras funciones; las demás, quienes iniciaron sesión.
revoke all on function public.usuario_de_sesion() from public, anon, authenticated;
revoke all on function public.revisar_conversacion(bigint, bigint) from public, anon, authenticated;
revoke all on function public.json_mensaje(public.mensajes) from public, anon, authenticated;
revoke all on function public.accion_pendiente(bigint, bigint) from public, anon, authenticated;
revoke all on function public.conversaciones_asistente() from public, anon;
revoke all on function public.mensajes_asistente(bigint) from public, anon;
revoke all on function public.guardar_intercambio_asistente(bigint, text, text, jsonb) from public, anon;
revoke all on function public.eliminar_conversacion_asistente(bigint) from public, anon;
revoke all on function public.confirmar_accion_asistente(bigint) from public, anon;
revoke all on function public.cancelar_accion_asistente(bigint) from public, anon;
revoke all on function public.movimientos_asistente(date, date) from public, anon;
grant execute on function public.conversaciones_asistente() to authenticated;
grant execute on function public.mensajes_asistente(bigint) to authenticated;
grant execute on function public.guardar_intercambio_asistente(bigint, text, text, jsonb) to authenticated;
grant execute on function public.eliminar_conversacion_asistente(bigint) to authenticated;
grant execute on function public.confirmar_accion_asistente(bigint) to authenticated;
grant execute on function public.cancelar_accion_asistente(bigint) to authenticated;
grant execute on function public.movimientos_asistente(date, date) to authenticated;
