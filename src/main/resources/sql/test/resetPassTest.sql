SET DATABASE SQL SYNTAX PGS TRUE;

INSERT INTO public.role(
            role_id, name)
    VALUES (1, 'ROLE_EMPLOYEE');

INSERT INTO public.person(
            person_id, first_name, last_name, email, password, role_id, enabled)
    VALUES (1, 'Test', 'Test', 'test@gmail.com', 'password', 1, TRUE );

INSERT INTO public.person(
            person_id, first_name, last_name, email, password, role_id, enabled)
   VALUES (2, 'Test1', 'Test1', 'test@gmail.com1', 'password1', 1, TRUE );
