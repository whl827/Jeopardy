DROP DATABASE if exists Users;
DROP TABLE if exists Users.UserInfo;

CREATE DATABASE Users;

USE Users;

CREATE TABLE UserInfo (
  userID int(11) primary key not null auto_increment,
  user_name varchar(21) not null,
  pass_word varchar(255) not null
);