-- 샘플
create table sample
(
    id   bigint auto_increment primary key,
    name varchar(255) null
);

-- 유저
create table users
(
    id                bigint auto_increment primary key,
    created_at        timestamp(6)                     not null,
    updated_at        timestamp(6)                     not null,
    provider_username varchar(30)                      not null,
    created_by        varchar(50)                      null,
    email             varchar(50)                      null,
    nickname          varchar(50)                      not null,
    updated_by        varchar(50)                      null,
    provider          varchar(50)                      not null,
    role              varchar(50) default 'USER'       not null,
    entity_status     varchar(50) default 'ACTIVE'     not null,
    time_zone         varchar(50) default 'Asia/Seoul' not null,
    constraint uk_nickname
        unique (nickname),
    constraint uk_provider_provider_username
        unique (provider, provider_username)
);


-- 발자취
create table footstep
(
    id            bigint auto_increment primary key,
    user_id       bigint                                    not null,
    date          date                                      not null,
    content       varchar(50)                               null,
    image_url     varchar(255)                              not null,
    entity_status varchar(50)  default 'ACTIVE'             not null,
    created_at    timestamp(6) default CURRENT_TIMESTAMP(6) not null,
    updated_at    timestamp(6) default CURRENT_TIMESTAMP(6) not null
);

