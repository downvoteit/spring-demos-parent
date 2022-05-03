insert into items_categories (id, name, amount, price)
values (1, 'Primary', 0, 0),
       (2, 'Secondary', 0, 0),
       (3, 'Tertiary', 0, 0);

alter sequence items_categories_id_seq restart with 4;
