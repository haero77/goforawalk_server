-- 리프레쉬 토큰
create table refresh_token
(
    id         bigint auto_increment primary key,
    user_id    bigint                                    not null,
    token      varchar(512)                              not null,
    created_at timestamp(6) default current_timestamp(6) not null,
    updated_at timestamp(6) default current_timestamp(6) not null
)