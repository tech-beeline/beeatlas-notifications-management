DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM notification.business_event_enum LIMIT 1)
    THEN
        INSERT INTO notification.business_event_enum (id, name, description)
        VALUES
            (1, 'TECH_CAPABILITY', 'тех возможности'),
            (2, 'BUSINESS_CAPABILITY', 'бизнес возможности'),
            (3, 'TECH', 'технологии'),
            (4, 'tech', 'экспорт технологий'),
            (5, 'business-capability', 'экспорт BC'),
            (6, 'tech-capability', 'экспорт TC'),
            (7, 'create_business_capability', 'Обновление заявки на создание бизнес-возможности'),
            (8, 'update_business_capability', 'Обновление заявки на изменение бизнес-возможности');
END IF;
END $$;