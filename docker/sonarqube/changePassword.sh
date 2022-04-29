#!/usr/bin/env bash

\set ON_ERROR_STOP true

if [[ $(pg_isready -U "$POSTGRES_USER" -d "$POSTGRES_DB") != *"accepting connections"* ]]; then
  exit 1
fi

psql -v ON_ERROR_STOP=1 -U "$POSTGRES_USER" -w <<EOF
do
\$\$
declare
  lookup_table varchar = 'users';
begin
  if exists(select *
              from information_schema.tables
              where table_schema = current_schema()
                and table_name = lookup_table) then
    update users
      set crypted_password = '$ADMIN_PASSWORD',
          salt = null,
          hash_method = 'BCRYPT',
          active = true,
          reset_password = false
      where login = 'admin'
        and hash_method != 'BCRYPT';
  end if;
end
\$\$
language plpgsql;
EOF
