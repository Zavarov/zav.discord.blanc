DELETE FROM 'Webhook'
WHERE guildId IS %s AND channelId NOT IN (%s);