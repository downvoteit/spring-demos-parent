create table categories
(
    id   serial not null
        constraint categories_pkey primary key,
    name varchar(255),
    unique (name)
);

create table items
(
    id          serial not null
        constraint items_pkey primary key,
    category_id int,
    name        varchar(255),
    amount      integer default 0,
    price       double precision,
    unique (name),
    constraint items_fkey
        foreign key (category_id)
            references categories (id)
);

alter table categories
    owner to postgres;
alter table items
    owner to postgres;
