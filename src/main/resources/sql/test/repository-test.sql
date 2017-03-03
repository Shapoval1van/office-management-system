SET DATABASE SQL SYNTAX PGS TRUE;
DELETE FROM REQUEST;
DELETE FROM PERSON;
DELETE FROM FIELD;
DELETE FROM REQUEST_GROUP;
DELETE FROM ROLE;
DELETE FROM STATUS;
DELETE FROM PRIORITY;

INSERT INTO public.role (role_id, name) VALUES
  (1, 'ROLE_ADMINISTRATOR'),
  (2, 'ROLE_OFFICE MANAGER'),
  (3, 'ROLE_EMPLOYEE');

INSERT INTO public.status (status_id, name) VALUES
  (1, 'FREE'),
  (2, 'IN PROGRESS'),
  (3, 'CLOSED'),
  (4, 'REOPEN'),
  (5, 'CANCELED');

INSERT INTO public.priority (priority_id, name) VALUES
  (1, 'HIGH'),
  (2, 'NORMAL'),
  (3, 'LOW');

INSERT INTO public.field (field_id, name) VALUES
  (1, 'name'),
  (2, 'description'),
  (3, 'status_id'),
  (4, 'manager_id'),
  (5, 'priority_id'),
  (6, 'group_id');




INSERT INTO public.person (person_id, first_name, last_name, email, password, role_id, enabled) VALUES
(
  2,
  'MANAGER-  2',
  'SMITH-  2',
  'test2@test.com',
  'test2',
  2,
  TRUE
);


INSERT INTO public.request_group (request_group_id, name) VALUES( 1, 'Request group   1');


INSERT INTO public.request (request_id, name, description, creation_time, status_id, employee_id, priority_id) VALUES
(
    1,
  'Request 1',
  'I want 1 cup of coffee',
  TIMESTAMP  '2017-02-24 00:59:02.184181',
  2,
  2,
  2
);

INSERT INTO public.request (request_id, name, description, creation_time, status_id, employee_id, priority_id) VALUES
(
    2,
  'Request 2 (closed)',
  'Request 2 description',
  TIMESTAMP  '2017-02-24 00:59:02.184181',
  3,
  2,
  2
);

INSERT INTO public.request (request_id, name, description, creation_time, status_id, employee_id, priority_id) VALUES
(
    3,
  'Request test',
  'Request test description',
  TIMESTAMP  '2017-02-24 00:59:02.184181',
  1,
  2,
  2
);

INSERT INTO public.request (request_id, name, description, creation_time, status_id, employee_id, priority_id, parent_id) VALUES
(
    4,
  'Sub request',
  'Sub request test description',
  TIMESTAMP  '2017-02-24 00:59:02.184181',
  1,
  2,
  2,
  3
);


INSERT INTO comment(comment_id, body, request_id, author_id, publish_date) VALUES
(1, 'Body', 1, 2, '2017-01-01 00:00:00.000000');
