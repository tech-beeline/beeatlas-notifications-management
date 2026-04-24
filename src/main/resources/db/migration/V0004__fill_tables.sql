DO $$
DECLARE
seq_start_value INTEGER;
BEGIN
SELECT COALESCE(MAX(id), 0) + 1 INTO seq_start_value
FROM notification.business_event_enum;

EXECUTE format('
        CREATE SEQUENCE IF NOT EXISTS notification.business_event_enum_id_seq
        START WITH %s
        INCREMENT BY 1
        OWNED BY notification.business_event_enum.id
    ', seq_start_value);
END $$;ALTER TABLE notification.business_event_enum
    ALTER COLUMN id SET DEFAULT nextval('notification.business_event_enum_id_seq');INSERT INTO notification.business_event_enum (name, description)
                                                                                   VALUES
                                                                                       ('create_business_capability', 'Обновление заявки на создание бизнес-возможности'),
                                                                                       ('update_business_capability', 'Обновление заявки на изменение бизнес-возможности');