\c grabber
create table if not exists post (
    id serial primary key,
    name text,
    description text,
    link text,
    created timestamp,
    primary key(id, link)
);
