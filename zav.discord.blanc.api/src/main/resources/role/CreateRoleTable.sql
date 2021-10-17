CREATE TABLE IF NOT EXISTS 'Role' (
    'id' INTEGER NOT NULL,
    'guildId' INTEGER NOT NULL,
    'name' TEXT NOT NULL,
    'group' TEXT,
    PRIMARY KEY ('id', 'guildId')
);