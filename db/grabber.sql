\c grabber
create table if not exists posts (
    id serial primary key,
    name text,
    description text,
    link text unique,
    created timestamp,
    updated timestamp
);
