SET DATABASE SQL SYNTAX PGS TRUE;
DELETE FROM TOKEN;
DELETE FROM PERSON;
DELETE FROM ROLE;

INSERT INTO public.role(
            role_id, name)
    VALUES (100, 'ROLE_EMPLOYEE');

INSERT INTO public.person(
            person_id, first_name, last_name, email, password, role_id, enabled)
    VALUES (100, 'Anatolii', 'Syvenko', 'a.p.syvenko@gmail.com', 'password', 100, FALSE );

INSERT INTO public.person(
            person_id, first_name, last_name, email, password, role_id, enabled)
    VALUES (101, 'Enabled', 'Person', 'enabled@mail.com', 'password', 100, TRUE );

INSERT INTO public.token(
            token_id, token, person_id, token_type, date_expired)
    VALUES (100, 'TEST_TOKEN', 100, 1, now() + INTERVAL '1' DAY );