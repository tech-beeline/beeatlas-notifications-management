DO
$$
BEGIN
    IF
NOT EXISTS (SELECT 1 FROM notification.entity_type_enum LIMIT 1)
    THEN
        INSERT INTO notification.entity_type_enum (id, type, alias, base_link_template)
        VALUES
            (1, 'TECH_CAPABILITY', 'Техническая возможность', '/models/fdm?id=<id>&type=TECH'),
            (2, 'BUSINESS_CAPABILITY', 'Бизнес-возможность', '/models/fdm?id=<id>&type=BUSINESS'),
            (3, 'TECH', 'Технолоджии', '/models/tech-radar?id=<id>'),
            (4, 'arch_interface', 'Интерфейс', '/models/apps/view?tab=INTERFACES_AND_METHODS&subtab=Structurizr&type=arch_interface&id=<id>&hideEmpty=true&hideDeleted=true');
END IF;
END $$;