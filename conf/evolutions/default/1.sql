# --- !Ups

CREATE TABLE "passwordinfo" (
  hasher varchar NOT NULL,
  password varchar NOT NULL,
  userID varchar NOT NULL
);

CREATE TABLE "logininfo" (
  userID varchar NOT NULL,
  providerID varchar NOT NULL,
  providerKey varchar NOT NULL,
  PRIMARY KEY (userID)
);

CREATE TABLE "administrators" (
  userID varchar NOT NULL,
  title varchar NOT NULL,
  firstName varchar NOT NULL,
  lastName varchar NOT NULL,
  email varchar,
  PRIMARY KEY (userID)
);

CREATE TABLE "prescribers" (
  userID varchar NOT NULL,
  title varchar NOT NULL,
  firstName varchar NOT NULL,
  lastName varchar NOT NULL,
  email varchar,
  PRIMARY KEY (userID)
);

CREATE TABLE "patients" (
  hospital_number varchar NOT NULL,
  title varchar NOT NULL,
  firstname varchar NOT NULL,
  surname varchar NOT NULL,
  dob varchar NOT NULL,
  PRIMARY KEY (hospital_number)
);

CREATE TABLE "prescriptions" (
  pt_hospital_number varchar NOT NULL,
  prescriberid varchar NOT NULL,
  date timestamp NOT NULL,
  mr_drug varchar NOT NULL,
  mr_dose DOUBLE PRECISION NOT NULL,
  breakthrough_drug varchar NOT NULL,
  breakthrough_dose DOUBLE PRECISION NOT NULL,
  PRIMARY KEY (pt_hospital_number, date),
  FOREIGN KEY (pt_hospital_number) REFERENCES patients(hospital_number),
  FOREIGN KEY (prescriberid) REFERENCES prescribers(userID)
);

CREATE TABLE "doses" (
  pt_hospital_number varchar NOT NULL,
  date timestamp NOT NULL,
  PRIMARY KEY (pt_hospital_number, date),
  FOREIGN KEY (pt_hospital_number) REFERENCES patients(hospital_number),
  FOREIGN KEY (pt_hospital_number) REFERENCES prescriptions(pt_hospital_number)
);

# --- !Downs

-- drop table "doses";
-- drop table "prescriptions";
-- drop table "patients";
-- drop table "prescribers";
-- drop table "administrators";
-- drop table "logininfo";
-- drop table "passwordinfo";

