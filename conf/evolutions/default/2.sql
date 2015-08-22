# --- !Ups

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
  prescriberID varchar NOT NULL,
  date timestamp NOT NULL,
  mr_drug varchar NOT NULL,
  mr_dose DOUBLE PRECISION NOT NULL,
  breakthrough_drug varchar NOT NULL,
  breakthrough_dose DOUBLE PRECISION NOT NULL,
  PRIMARY KEY (pt_hospital_number),
  FOREIGN KEY (pt_hospital_number) REFERENCES patients(hospital_number),
  FOREIGN KEY (prescriberID) REFERENCES prescribers(userID)
);

# --- !Downs

-- drop table "prescribers";
-- drop table "prescriptions";
-- drop table "patients";