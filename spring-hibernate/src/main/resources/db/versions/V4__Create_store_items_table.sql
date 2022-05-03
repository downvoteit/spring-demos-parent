create table store_items
(
    id          serial not null
        constraint store_items_pkey primary key,
    category_id int,
    name        varchar(255),
    store_name  varchar(255),
    amount      integer default 0,
    price       double precision,
    unique (name),
    constraint store_items_fkey
        foreign key (category_id)
            references categories (id)
);

create table warehouse_items
(
    id             serial not null
        constraint warehouse_items_pkey primary key,
    category_id    int,
    name           varchar(255),
    warehouse_name varchar(255),
    amount         integer default 0,
    price          double precision,
    unique (name),
    constraint warehouse_items_fkey
        foreign key (category_id)
            references categories (id)
);

alter table store_items
    owner to postgres;
alter table warehouse_items
    owner to postgres;


