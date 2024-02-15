-- Script de criação da tabela de usuarios:
CREATE TABLE USERS (
                       id int8 not null primary key auto_increment,
                       name varchar(100) not null,
                       age integer not null
)

-- Script de criação da tabela de posts:
CREATE TABLE POSTS (
                       id int8 not null primary key auto_increment,
                       post_text varchar(150) not null,
                       date_time timestamp,
                       user_id int not null references USERS(id)
)