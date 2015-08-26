# --- !Ups

CREATE TABLE "doses" (
  pt_hospital_number varchar NOT NULL,
  date timestamp NOT NULL,
  PRIMARY KEY (pt_hospital_number),
  FOREIGN KEY (pt_hospital_number) REFERENCES patients(hospital_number)
);

# --- !Downs

-- drop table "doses";