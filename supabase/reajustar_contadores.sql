-- Solo hace falta si se importaron datos desde CSV (con sus id).
--
-- Al importar filas con su id, el contador de cada tabla no se entera y el siguiente registro
-- nuevo intentaría usar un id que ya existe. Esto deja cada contador después del id más alto.
-- Se puede correr las veces que haga falta.

do $$
declare
  columna record;
begin
  for columna in
    select table_name, column_name
    from information_schema.columns
    where table_schema = 'public' and is_identity = 'YES'
  loop
    execute format(
      'select setval(pg_get_serial_sequence(%L, %L), coalesce((select max(%I) from public.%I), 0) + 1, false)',
      'public.' || columna.table_name, columna.column_name, columna.column_name, columna.table_name
    );
  end loop;
end $$;
