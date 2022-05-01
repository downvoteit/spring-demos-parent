insert into categories (id, name)
values (1, 'Primary'),
       (2, 'Secondary'),
       (3, 'Tertiary');

alter sequence categories_id_seq restart with 4;
