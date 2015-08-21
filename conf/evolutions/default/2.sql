# --- !Ups

CREATE TABLE "patients" (
  hospitalNumber varchar NOT NULL,
  title varchar NOT NULL,
  firstname varchar NOT NULL,
  surname varchar NOT NULL,
  dob varchar,
  PRIMARY KEY (hospitalNumber)
);

# --- !Downs

-- drop table "patients";