CREATE TABLE IF NOT EXISTS 'Webhook' (
    'guildId' INTEGER NOT NULL,
    'channelId' INTEGER NOT NULL,
    'id' INTEGER NOT NULL,
    'name' TEXT NOT NULL,
    'subreddits' TEXT NOT NULL,
    'owner' BOOLEAN NOT NULL,
    PRIMARY KEY ('guildId', 'channelId', 'id')
);