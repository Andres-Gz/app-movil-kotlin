-- Resumen de la pantalla de inicio (el dashboard).
--
-- Se corre una vez en el SQL Editor del proyecto (después de configuracion_cuentas.sql).
-- Se puede volver a correr sin problema.
--
-- Junta en una sola llamada todo lo que muestra el inicio, solo del usuario de la sesión:
-- su nombre, su cuenta principal, los totales de ingresos y gastos, los 4 últimos movimientos
-- y su meta más reciente con lo que lleva ahorrado. Las tablas siguen cerradas por RLS.

create or replace function public.resumen_inicio()
returns json
language plpgsql
stable
security definer
set search_path = public
as $$
declare
  v_usuario bigint;
  v_nombre text;
begin
  -- El usuario sale de la sesión (el token), nunca de lo que mande la app.
  select id_usuario, nombre into v_usuario, v_nombre
  from usuarios
  where lower(correo) = lower(auth.jwt() ->> 'email');

  if v_usuario is null then
    raise exception 'No hay un perfil para esta sesión' using errcode = '42501';
  end if;

  return json_build_object(
    'nombre', v_nombre,
    'cuenta', (
      select nombre from cuentas
      where id_usuario = v_usuario and activo
      order by id_cuenta
      limit 1
    ),
    'saldo_inicial', coalesce((
      select sum(saldo_inicial) from cuentas where id_usuario = v_usuario and activo
    ), 0),
    'ingresos', coalesce((
      select sum(monto) from movimientos
      where id_usuario = v_usuario and activo and lower(tipo) = 'ingreso'
    ), 0),
    'gastos', coalesce((
      select sum(monto) from movimientos
      where id_usuario = v_usuario and activo and lower(tipo) = 'gasto'
    ), 0),
    'total_movimientos', (
      select count(*) from movimientos where id_usuario = v_usuario and activo
    ),
    'movimientos', coalesce((
      select json_agg(ultimo)
      from (
        select m.id_movimiento, lower(m.tipo) as tipo, m.monto, m.descripcion, m.fecha,
               c.nombre as categoria, cu.nombre as cuenta
        from movimientos m
        left join categorias c on c.id_categoria = m.id_categoria
        left join cuentas cu on cu.id_cuenta = m.id_cuenta
        where m.id_usuario = v_usuario and m.activo
        order by m.fecha desc, m.id_movimiento desc
        limit 4
      ) as ultimo
    ), '[]'::json),
    'meta', (
      select json_build_object(
        'nombre', me.nombre,
        'objetivo', me.monto_objetivo,
        'ahorrado', coalesce((select sum(a.monto) from aportes a where a.id_meta = me.id_meta), 0)
      )
      from metas me
      where me.id_usuario = v_usuario and me.activo
      order by me.fecha_creacion desc
      limit 1
    )
  );
end;
$$;

-- Solo la pueden usar quienes iniciaron sesión.
revoke all on function public.resumen_inicio() from public, anon;
grant execute on function public.resumen_inicio() to authenticated;
