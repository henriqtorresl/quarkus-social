-- Script de criação da tabela de usuarios:
CREATE TABLE USERS (
                       id int8 not null primary key auto_increment,
                       name varchar(100) not null,
                       age integer not null
)