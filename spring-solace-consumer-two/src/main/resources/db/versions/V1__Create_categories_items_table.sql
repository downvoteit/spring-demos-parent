create table items_categories
(
    id     serial not null
        constraint items_categories_pkey primary key,
    name   varchar(255),
    amount integer default 0,
    price  double precision
);

alter table items_categories
    owner to postgres;
