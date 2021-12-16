CREATE TABLE IF NOT EXISTS 'TextChannel' (
    'id' INTEGER NOT NULL,
    'guildId' INTEGER NOT NULL,
    'name' TEXT NOT NULL,
    'subreddits' TEXT NOT NULL,
    PRIMARY KEY ('id', 'guildId')
);