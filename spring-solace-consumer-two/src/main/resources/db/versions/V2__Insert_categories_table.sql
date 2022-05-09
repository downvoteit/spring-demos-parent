insert into items_categories (id, name, amount, price)
values (1, 'Primary', 1, 10),
       (2, 'Secondary', 1, 10),
       (3, 'Tertiary', 1, 10);

alter sequence items_categories_id_seq restart with 4;
