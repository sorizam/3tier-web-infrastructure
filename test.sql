
CREATE DATABASE web_db;

CREATE TABLE web_db.user (
    id int primary key,
    name varchar(50),
    age int
);

INSERT INTO web_db.user VALUES(1,'hong', 24);

SELECT * FROM web_db.user;
