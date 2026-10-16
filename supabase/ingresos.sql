-- Registrar ingresos desde la app.
--
-- Se corre una vez en el SQL Editor del proyecto (después de configuracion_cuentas.sql).
-- Se puede volver a correr sin problema.
--
-- La app no escribe directo en `cuentas` ni en `movimientos` (siguen cerradas por RLS): llama a esta
-- función, que trabaja solo con el usuario de la sesión. Si el usuario todavía no tiene cuenta,
-- le crea una "Cuenta principal" y ahí guarda el ingreso. La frecuencia queda en la descripción
-- del movimiento ("Ingreso mensual", "Ingreso quincenal"…), porque la tabla no tiene esa columna.

create or replace function public.registrar_ingreso(p_monto numeric, p_frecuencia text)
returns bigint
language plpgsql
security definer
set search_path = public
as $$
declare
  v_usuario bigint;
  v_cuenta bigint;
  v_movimiento bigint;
begin
  -- El usuario sale de la sesión (el token), nunca de lo que mande la app.
  select id_usuario into v_usuario
  from usuarios
  where lower(correo) = lower(auth.jwt() ->> 'email');

  if v_usuario is null then
    raise exception 'No hay un perfil para esta sesión' using errcode = '42501';
  end if;
  if p_monto is null or p_monto <= 0 then
    raise exception 'El monto debe ser mayor que cero' using errcode = '22023';
  end if;
  if p_frecuencia is null or p_frecuencia not in ('mensual', 'quincenal', 'semanal', 'diario') then
    raise exception 'Frecuencia no válida' using errcode = '22023';
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

  insert into movimientos (id_usuario, id_cuenta, tipo, monto, descripcion)
  values (v_usuario, v_cuenta, 'ingreso', p_monto, 'Ingreso ' || p_frecuencia)
  returning id_movimiento into v_movimiento;

  return v_movimiento;
end;
$$;

-- Solo la pueden usar quienes iniciaron sesión.
revoke all on function public.registrar_ingreso(numeric, text) from public, anon;
grant execute on function public.registrar_ingreso(numeric, text) to authenticated;
