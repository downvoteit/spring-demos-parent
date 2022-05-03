insert into items (id, category_id, name, amount, price)
values (nextval('items_id_seq'), 1, 'Test primary', 1, 10.0),
       (nextval('items_id_seq'), 2, 'Test secondary', 2, 20.0),
       (nextval('items_id_seq'), 3, 'Test tertiary', 3, 30.0);
