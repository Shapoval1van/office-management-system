SET DATABASE SQL SYNTAX PGS TRUE;
DELETE FROM REQUEST;
DELETE FROM REQUEST_GROUP;
DELETE FROM PERSON;
DELETE FROM FIELD;
DELETE FROM REQUEST_GROUP;
DELETE FROM ROLE;
DELETE FROM STATUS;
DELETE FROM PRIORITY;
DELETE FROM COMMENT;

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
  (1, 'NAME'),
  (2, 'DESCRIPTION'),
  (3, 'STATUS'),
  (4,'MANAGER'),
  (5,'PRIORITY'),
  (6,'ESTIMATE'),
  (7,'GROUP');



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

INSERT INTO public.person (person_id, first_name, last_name, email, password, role_id, enabled) VALUES
  (
    3,
    'MANAGER-  2',
    'SMITH-  2',
    'test4@test.com',
    'test2',
    1,
    TRUE
  );

INSERT INTO public.request_group (request_group_id, name, author_id) VALUES (1, 'Request group   1', 2);


INSERT INTO public.request (request_id, name, description, creation_time, status_id, employee_id, priority_id) VALUES
  (
    1,
    'Request 1',
    'I want 1 cup of coffee',
    TIMESTAMP '2017-02-24 00:59:02.184181',
    2,
    2,
    2
  );

INSERT INTO public.request (request_id, name, description, creation_time, status_id, employee_id, priority_id) VALUES
  (
    2,
    'Request 2 (closed)',
    'Request 2 description',
    TIMESTAMP '2017-02-24 00:59:02.184181',
    3,
    2,
    2
  );

INSERT INTO public.request (request_id, name, description, creation_time, status_id, employee_id, priority_id, manager_id) VALUES
  (
    3,
    'Request test',
    'Request test description',
    TIMESTAMP '2017-02-24 00:59:02.184181',
    1,
    2,
    2,
    2
  );

INSERT INTO public.request (request_id, name, description, creation_time, status_id, employee_id, priority_id, parent_id)
VALUES
  (
    4,
    'Sub request',
    'Sub request test description',
    TIMESTAMP '2017-02-24 00:59:02.184181',
    1,
    2,
    2,
    3
  );

INSERT INTO public.change_group (change_group_id, created, author_id, request_id) VALUES
  (
    2,
    TIMESTAMP '2017-02-24 00:59:02.184181',
    2,
    3
  );

INSERT INTO public.change_group (change_group_id, created, author_id, request_id) VALUES
  (
    3,
    TIMESTAMP '2017-02-24 00:59:02.184181',
    2,
    3
  );

INSERT INTO public.change_group (change_group_id, created, author_id, request_id) VALUES
  (
    4,
    DATE '2017-03-02',
    2,
    3
  );

INSERT INTO public.change_item (change_item_id, old_value, new_value, change_group_id, field_id) VALUES
  (
    1,
    'old1',
    'new1',
    2,
    3
  );

INSERT INTO public.change_item (change_item_id, old_value, new_value, change_group_id, field_id) VALUES
  (
    2,
    'old2',
    'new2',
    2,
    4
  );

INSERT INTO comment (comment_id, body, request_id, author_id, publish_date) VALUES
  (1, 'Body', 1, 2, '2017-01-01 00:00:00.000000');