alter table users
    alter column role set default 'USER';

alter table users
    add column entity_status enum ('ACTIVE','DELETED') default 'ACTIVE' not null;

alter table users
    add column time_zone varchar(50) default 'Asia/Seoul' not null;


create table footstep
(
    id            bigint auto_increment,
    user_id       bigint                                    not null,
    issue_date    date                                      not null,
    content       varchar(50),
    image_url     varchar(255)                              not null,
    entity_status enum ('ACTIVE','DELETED')                 not null,
    created_at    timestamp(6) default current_timestamp(6) not null,
    updated_at    timestamp(6) default current_timestamp(6) not null,
    primary key (id)
);

