package vartas.discord.blanc.command.legacy;

import vartas.discord.blanc.Architecture.TextChannel;
import vartas.discord.blanc.Architecture.Role;

group legacy {
    command reddit requires Guild{
             class : RedditCommand
         parameter : String subreddit, TextChannel textChannel?
        permission : MANAGE_CHANNELS, MANAGE_WEBHOOKS
    }
}
