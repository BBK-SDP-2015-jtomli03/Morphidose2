# --- !Ups

CREATE TABLE "user" (
  userID varchar NOT NULL,
  firstName varchar,
  lastName varchar,
  fullName varchar,
  email varchar,
  PRIMARY KEY (userID)
);

CREATE TABLE logininfo (
  userID varchar NOT NULL,
  providerID varchar NOT NULL,
  providerKey varchar NOT NULL,
  PRIMARY KEY (userID)
);

CREATE TABLE passwordinfo (
  hasher varchar NOT NULL,
  password varchar NOT NULL,
  userID varchar NOT NULL
);

# --- !Downs

-- drop table "passwordinfo";
-- drop table "logininfo";
-- drop table "user";