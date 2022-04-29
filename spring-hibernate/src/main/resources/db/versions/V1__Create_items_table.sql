create table items
(
    id     serial not null
        constraint items_pkey primary key,
    name   varchar(255),
    amount integer default 0,
    price  double precision,
    unique (name)
);

alter table items
    owner to postgres;
