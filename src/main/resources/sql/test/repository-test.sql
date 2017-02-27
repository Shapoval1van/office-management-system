INSERT INTO role (role_id, name) VALUES
  (1, 'ROLE_ADMINISTRATOR'),
  (2, 'ROLE_OFFICE MANAGER'),
  (3, 'ROLE_EMPLOYEE');

INSERT INTO status (status_id, name) VALUES
  (1, 'FREE'),
  (2, 'IN PROGRESS'),
  (3, 'CLOSED'),
  (4, 'REOPEN');

INSERT INTO priority (priority_id, name) VALUES
  (1, 'HIGH'),
  (2, 'NORMAL'),
  (3, 'LOW');

INSERT INTO field (field_id, name) VALUES
  (1, 'name'),
  (2, 'description'),
  (3, 'status_id'),
  (4, 'manager_id'),
  (5, 'priority_id'),
  (6, 'group_id');




INSERT INTO person (person_id, first_name, last_name, email, password, role_id, enabled) VALUES
(
  2,
  'MANAGER-  2',
  'SMITH-  2',
  'test2@test.com',
  'test2',
  2,
  TRUE
);


INSERT INTO request_group (request_group_id, name) VALUES( 1, 'Request group   1');


INSERT INTO request (request_id, name, description, creation_time, status_id, employee_id, priority_id) VALUES
(
    1,
  'Request    1',
  'I want    1 cup of coffee',
  TIMESTAMP  '2017-02-24 00:59:02.184181',
  2,
  2,
  2
);