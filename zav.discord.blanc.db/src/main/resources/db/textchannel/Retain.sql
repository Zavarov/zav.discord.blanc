DELETE FROM 'TextChannel'
WHERE guildId IS %s AND id NOT IN (%s);