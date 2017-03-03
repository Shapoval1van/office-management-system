ALTER TABLE IF EXISTS CHANGE_GROUP DROP CONSTRAINT AUTHOR_FK;
ALTER TABLE IF EXISTS REQUEST DROP CONSTRAINT EMPLOYEE_FK;
ALTER TABLE IF EXISTS CHANGE_ITEM DROP CONSTRAINT FIELD_FK;
ALTER TABLE IF EXISTS REQUEST DROP CONSTRAINT GROUP_FK;
ALTER TABLE IF EXISTS REQUEST DROP CONSTRAINT MANAGER_FK;
ALTER TABLE IF EXISTS CHANGE_GROUP DROP CONSTRAINT REQUEST_FK;
ALTER TABLE IF EXISTS PERSON DROP CONSTRAINT ROLE_FK;
ALTER TABLE IF EXISTS REQUEST DROP CONSTRAINT STATUS_FK;
ALTER TABLE IF EXISTS CHANGE_ITEM DROP CONSTRAINT group_fkv2;
ALTER TABLE IF EXISTS REQUEST DROP CONSTRAINT parent_request_fk;
ALTER TABLE IF EXISTS REQUEST DROP CONSTRAINT priority_FK;
ALTER TABLE IF EXISTS TOKEN DROP CONSTRAINT PERSON_FK;
ALTER TABLE IF EXISTS COMMENT DROP CONSTRAINT COMMENT_AUTHOR_FK;
ALTER TABLE IF EXISTS COMMENT DROP CONSTRAINT COMMENT_REQUEST_FK;

DROP TABLE IF EXISTS CHANGE_GROUP;
DROP TABLE IF EXISTS CHANGE_ITEM;
DROP TABLE IF EXISTS FIELD;
DROP TABLE IF EXISTS REQUEST_GROUP;
DROP TABLE IF EXISTS PRIORITY;
DROP TABLE IF EXISTS REQUEST;
DROP TABLE IF EXISTS ROLE;
DROP TABLE IF EXISTS STATUS;
DROP TABLE IF EXISTS TOKEN;
DROP TABLE IF EXISTS PERSON CASCADE;
DROP TABLE IF EXISTS COMMENT;

CREATE TABLE CHANGE_GROUP
(
  change_group_id BIGSERIAL                NOT NULL,
  created         TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
  author_id       BIGINT                   NOT NULL,
  request_id      BIGINT                   NOT NULL
);
ALTER TABLE CHANGE_GROUP
  ADD CONSTRAINT CHANGE_GROUP_PK PRIMARY KEY (change_group_id);


CREATE TABLE CHANGE_ITEM
(
  change_item_id  BIGSERIAL NOT NULL,
  old_value       TEXT,
  new_value       TEXT,
  change_group_id BIGINT    NOT NULL,
  field_id        INT       NOT NULL
);
ALTER TABLE CHANGE_ITEM
  ADD CONSTRAINT HISTORY_PK PRIMARY KEY (change_item_id);


CREATE TABLE FIELD
(
  field_id SERIAL      NOT NULL,
  name     VARCHAR(20) NOT NULL
);
ALTER TABLE FIELD
  ADD CONSTRAINT FIELD_PK PRIMARY KEY (field_id);
ALTER TABLE FIELD
  ADD CONSTRAINT FIELD_UN UNIQUE (name);


CREATE TABLE REQUEST_GROUP
(
  request_group_id SERIAL NOT NULL,
  name             VARCHAR(20)
);
ALTER TABLE REQUEST_GROUP
  ADD CONSTRAINT GROUP_PK PRIMARY KEY (request_group_id);


CREATE TABLE PRIORITY
(
  priority_id SERIAL NOT NULL,
  name        VARCHAR(20)
);
ALTER TABLE PRIORITY
  ADD CONSTRAINT Priority_PK PRIMARY KEY (priority_id);
ALTER TABLE PRIORITY
  ADD CONSTRAINT PRIORITY_UN UNIQUE (name);


CREATE TABLE REQUEST
(
  request_id       BIGSERIAL                NOT NULL,
  name             VARCHAR(50)              NOT NULL,
  description      TEXT,
  creation_time    TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp,
  estimate         TIMESTAMP WITH TIME ZONE,
  status_id        INT                      NOT NULL,
  employee_id      BIGINT                   NOT NULL,
  manager_id       BIGINT,
  parent_id        BIGINT,
  priority_id      INT                      NOT NULL,
  request_group_id BIGINT
);
ALTER TABLE REQUEST
  ADD CONSTRAINT TASK_PK PRIMARY KEY (request_id);


CREATE TABLE ROLE
(
  role_id SERIAL NOT NULL,
  name    VARCHAR(20)
);
ALTER TABLE ROLE
  ADD CONSTRAINT ROLE_PK PRIMARY KEY (role_id);
ALTER TABLE ROLE
  ADD CONSTRAINT ROLE_UN UNIQUE (name);


CREATE TABLE STATUS
(
  status_id SERIAL NOT NULL,
  name      VARCHAR(20)
);
ALTER TABLE STATUS
  ADD CONSTRAINT STATUS_PK PRIMARY KEY (status_id);


CREATE TABLE PERSON
(
  person_id  BIGSERIAL   NOT NULL,
  first_name VARCHAR(50),
  last_name  VARCHAR(50),
  email      VARCHAR(50) NOT NULL,
  password   VARCHAR(70) NOT NULL,
  role_id    INT         NOT NULL,
  enabled    BOOLEAN     NOT NULL DEFAULT FALSE
);

ALTER TABLE PERSON
  ADD CONSTRAINT USER_PK PRIMARY KEY (person_id);
ALTER TABLE PERSON
  ADD CONSTRAINT PERSON_UN UNIQUE (email);

/* TABLE FOR TOKEN */

CREATE TABLE TOKEN
(
  token_id     BIGSERIAL NOT NULL PRIMARY KEY,
  token        VARCHAR(36),
  person_id    INT       NOT NULL,
  token_type   INT       NOT NULL,
  date_expired TIMESTAMP WITH TIME ZONE
);

ALTER TABLE TOKEN
  ADD CONSTRAINT PERSON_FK FOREIGN KEY (person_id) REFERENCES PERSON (person_id) ON DELETE CASCADE;

/* TABLE FOR TOKEN */

-- Table for comment
CREATE TABLE COMMENT (
  comment_id   BIGSERIAL                NOT NULL PRIMARY KEY,
  body         TEXT                     NOT NULL,
  request_id   BIGINT                   NOT NULL,
  author_id    BIGINT                   NOT NULL,
  publish_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT current_timestamp
);

ALTER TABLE COMMENT
  ADD CONSTRAINT COMMENT_REQUEST_FK FOREIGN KEY (request_id) REFERENCES REQUEST (request_id) ON DELETE CASCADE;

ALTER TABLE COMMENT
  ADD CONSTRAINT COMMENT_AUTHOR_FK FOREIGN KEY (author_id) REFERENCES PERSON (person_id) ON DELETE CASCADE;

ALTER TABLE CHANGE_GROUP
  ADD CONSTRAINT AUTHOR_FK FOREIGN KEY (author_id) REFERENCES PERSON (person_id) ON DELETE CASCADE;

ALTER TABLE REQUEST
  ADD CONSTRAINT EMPLOYEE_FK FOREIGN KEY (employee_id) REFERENCES PERSON (person_id) ON DELETE CASCADE;

ALTER TABLE CHANGE_ITEM
  ADD CONSTRAINT FIELD_FK FOREIGN KEY (field_id) REFERENCES FIELD (field_id) ON DELETE CASCADE;

ALTER TABLE REQUEST
  ADD CONSTRAINT GROUP_FK FOREIGN KEY (request_group_id) REFERENCES REQUEST_GROUP (request_group_id) ON DELETE SET NULL;

ALTER TABLE REQUEST
  ADD CONSTRAINT MANAGER_FK FOREIGN KEY (manager_id) REFERENCES PERSON (person_id) ON DELETE SET NULL;

ALTER TABLE CHANGE_GROUP
  ADD CONSTRAINT REQUEST_FK FOREIGN KEY (request_id) REFERENCES REQUEST (request_id) ON DELETE CASCADE;

ALTER TABLE PERSON
  ADD CONSTRAINT ROLE_FK FOREIGN KEY (role_id) REFERENCES ROLE (role_id);

ALTER TABLE REQUEST
  ADD CONSTRAINT STATUS_FK FOREIGN KEY (status_id) REFERENCES STATUS (status_id);

ALTER TABLE CHANGE_ITEM
  ADD CONSTRAINT group_fkv2 FOREIGN KEY (change_group_id) REFERENCES CHANGE_GROUP (change_group_id) ON DELETE CASCADE;

ALTER TABLE REQUEST
  ADD CONSTRAINT parent_request_fk FOREIGN KEY (parent_id) REFERENCES REQUEST (request_id) ON DELETE CASCADE;

ALTER TABLE REQUEST
  ADD CONSTRAINT priority_FK FOREIGN KEY (priority_id) REFERENCES PRIORITY (priority_id);