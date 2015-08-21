CREATE TABLE Department (
  id    INT         NOT NULL GENERATED ALWAYS AS IDENTITY,
  name  VARCHAR(50),
    PRIMARY KEY(id)
);

CREATE TABLE Employee (
  id           INT         NOT NULL GENERATED ALWAYS AS IDENTITY,
  departmentId INT,
  name         VARCHAR(50),
  sin          VARCHAR(11),
  wages        DOUBLE      NOT NULL DEFAULT 0,
  birthDate    DATE,
    FOREIGN KEY(departmentId) REFERENCES department(id),
    PRIMARY KEY(id)
);


CREATE TABLE EmployeeWithState (
  id           INT         NOT NULL GENERATED ALWAYS AS IDENTITY,
  departmentId INT,
  name         VARCHAR(50),
  sin          VARCHAR(11),
  wages        DOUBLE      NOT NULL DEFAULT 0,
    FOREIGN KEY(departmentId) REFERENCES department(id),
    PRIMARY KEY(id)
);


CREATE TABLE StringKey (
  id   VARCHAR(10),
  name VARCHAR(50),
    PRIMARY KEY(id)
);

INSERT INTO StringKey (id, name) VALUES ('aaa', 'name a');
INSERT INTO StringKey (id, name) VALUES ('bbb', 'name b');
INSERT INTO StringKey (id, name) VALUES ('ccc', 'name c');
INSERT INTO StringKey (id, name) VALUES ('ddd', 'name d');
INSERT INTO StringKey (id, name) VALUES ('eee', 'name e');

CREATE TABLE IntKey (
  id   INT         NOT NULL,
  name VARCHAR(50),
    PRIMARY KEY(id)
);

INSERT INTO IntKey (id, name) VALUES (1, 'name 1');
INSERT INTO IntKey (id, name) VALUES (2, 'name 2');
INSERT INTO IntKey (id, name) VALUES (3, 'name 3');
INSERT INTO IntKey (id, name) VALUES (4, 'name 4');
INSERT INTO IntKey (id, name) VALUES (5, 'name 5');







