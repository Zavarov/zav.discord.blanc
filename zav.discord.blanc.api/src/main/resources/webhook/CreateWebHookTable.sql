CREATE TABLE IF NOT EXISTS 'WebHook' (
    'id' INTEGER NOT NULL,
    'guildId' INTEGER NOT NULL,
    'channelId' INTEGER NOT NULL,
    'name' TEXT NOT NULL,
    'subreddits' TEXT NOT NULL,
    'owner' BOOLEAN NOT NULL,
    PRIMARY KEY ('id', 'guildId', 'channelId')
);