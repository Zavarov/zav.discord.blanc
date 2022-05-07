CREATE TABLE IF NOT EXISTS 'TextChannel' (
    'guildId' INTEGER NOT NULL,
    'id' INTEGER NOT NULL,
    'name' TEXT NOT NULL,
    'subreddits' TEXT NOT NULL,
    PRIMARY KEY ('guildId', 'id')
);