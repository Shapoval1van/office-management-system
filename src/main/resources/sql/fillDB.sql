INSERT INTO role (name) VALUES
  ('ADMINISTRATOR'),
  ('OFFICE MANAGER'),
  ('EMPLOYEE');

INSERT INTO status (name) VALUES
  ('FREE'),
  ('JOINED'),
  ('IN PROGRESS'),
  ('CLOSED'),
  ('REOPEN');

INSERT INTO priority (name) VALUES
  ('HIGH'),
  ('NORMAL'),
  ('LOW');

INSERT INTO field (name) VALUES
  ('name'),
  ('description'),
  ('status_id'),
  ('manager_id'),
  ('priority_id'),
  ('group_id');

DO
$$
BEGIN
  FOR i IN 1..10 LOOP
    INSERT INTO person (first_name, last_name, email, password, role_id, enabled) VALUES
      (
        CONCAT('MANAGER-', TO_CHAR(i, '99')),
        CONCAT('SMITH-', TO_CHAR(i, '99')),
        REPLACE(CONCAT('test', TO_CHAR(i, '99'), '@test.com'), ' ', ''),
        REPLACE(CONCAT('test', TO_CHAR(i, '99')), ' ', ''),
        2,
        TRUE
      );
  END LOOP;
END;
$$ LANGUAGE plpgsql;

DO
$$
BEGIN
  FOR i IN 1..20 LOOP
    INSERT INTO request_group (name) VALUES
      (
        CONCAT('Request group ', TO_CHAR(i, '99'))
      );
  END LOOP;
END;
$$ LANGUAGE plpgsql;

DO
$$
BEGIN
  FOR i IN 1..100 LOOP
    INSERT INTO request (name, description, creation_time, status_id, employee_id, priority_id) VALUES
      (
        CONCAT('Request ', TO_CHAR(i, '999')),
        CONCAT('I want ', TO_CHAR(i, '999'), ' cup of coffee'),
        current_timestamp + INTERVAL '1 hour',
        i % 5 + 1,
        i % 10 + 1,
        i % 3 + 1
      );
  END LOOP;
END;
$$ LANGUAGE plpgsql;

DO
$$
BEGIN
  FOR i IN 1..20 LOOP
    INSERT INTO change_group (created, author_id, request_id) VALUES
      (
        current_timestamp + INTERVAL '2 hour',
        i % 10 + 1,
        i % 10 + 1
      );
  END LOOP;
END;
$$ LANGUAGE plpgsql;

DO
$$
BEGIN
  FOR i IN 1..40 LOOP
    INSERT INTO change_item (old_value, new_value, change_group_id, field_id) VALUES
      (
        CONCAT('Old value ', TO_CHAR(i, '999')),
        CONCAT('New value ', TO_CHAR(i, '999')),
        i % 20 + 1,
        i % 6 + 1
      );
  END LOOP;
END;
$$ LANGUAGE plpgsql;