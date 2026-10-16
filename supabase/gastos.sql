-- Registrar gastos desde la app.
--
-- Se corre una vez en el SQL Editor del proyecto (después de configuracion_cuentas.sql).
-- Se puede volver a correr sin problema.
--
-- Igual que registrar_ingreso: la app no escribe directo en las tablas, llama a esta función, que
-- trabaja solo con el usuario de la sesión. Usa la cuenta principal del usuario (la crea si no
-- tiene) y la categoría elegida (la crea en `categorias` si el usuario todavía no la tiene).

create or replace function public.registrar_gasto(p_monto numeric, p_categoria text)
returns bigint
language plpgsql
security definer
set search_path = public
as $$
declare
  v_usuario bigint;
  v_cuenta bigint;
  v_categoria bigint;
  v_nombre text;
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

  v_nombre := case p_categoria
    when 'gastos_fijos' then 'Gastos fijos'
    when 'comida' then 'Comida'
    when 'diversion' then 'Diversión'
    when 'transporte' then 'Transporte'
  end;
  if v_nombre is null then
    raise exception 'Categoría no válida' using errcode = '22023';
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

  select id_categoria into v_categoria
  from categorias
  where id_usuario = v_usuario and tipo = 'gasto' and activo and lower(nombre) = lower(v_nombre)
  order by id_categoria
  limit 1;

  if v_categoria is null then
    insert into categorias (id_usuario, nombre, tipo)
    values (v_usuario, v_nombre, 'gasto')
    returning id_categoria into v_categoria;
  end if;

  insert into movimientos (id_usuario, id_cuenta, id_categoria, tipo, monto, descripcion)
  values (v_usuario, v_cuenta, v_categoria, 'gasto', p_monto, v_nombre)
  returning id_movimiento into v_movimiento;

  return v_movimiento;
end;
$$;

-- Solo la pueden usar quienes iniciaron sesión.
revoke all on function public.registrar_gasto(numeric, text) from public, anon;
grant execute on function public.registrar_gasto(numeric, text) to authenticated;
