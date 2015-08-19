# --- !Ups

CREATE TABLE "user" (
  userID varchar NOT NULL,
  title varchar NOT NULL,
  firstName varchar NOT NULL,
  lastName varchar NOT NULL,
  email varchar,
  PRIMARY KEY (userID)
);

CREATE TABLE logininfo (
  userID varchar NOT NULL,
  providerID varchar NOT NULL,
  providerKey varchar NOT NULL,
  usertype varchar NOT NULL,
  PRIMARY KEY (userID)
);

CREATE TABLE passwordinfo (
  hasher varchar NOT NULL,
  password varchar NOT NULL,
  userID varchar NOT NULL
);

CREATE TABLE administrators (
  userID varchar NOT NULL,
  title varchar NOT NULL,
  firstName varchar NOT NULL,
  lastName varchar NOT NULL,
  email varchar,
  PRIMARY KEY (userID)
);

# --- !Downs

-- drop table "passwordinfo";
-- drop table "logininfo";
-- drop table "user";