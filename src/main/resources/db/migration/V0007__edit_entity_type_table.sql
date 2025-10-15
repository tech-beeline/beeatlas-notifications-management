ALTER TABLE notification.entity_type_enum ADD COLUMN alias TEXT;

UPDATE notification.entity_type_enum
SET alias = 'Технологии'
WHERE type = 'TECH';

UPDATE notification.entity_type_enum
SET alias = 'Техническая возможность'
WHERE type = 'TECH_CAPABILITY';

UPDATE notification.entity_type_enum
SET alias = 'Бизнес-возможность'
WHERE type = 'BUSINESS_CAPABILITY';