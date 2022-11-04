package zav.discord.blanc.api.listener;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zav.discord.blanc.api.cache.AutoResponseCache;

/**
 * The listener for automatically responding to specific messages. Per guild, an arbitrary number
 * of regular expressions can be mapped to pre-defined strings. Whenever a message matches at least
 * one of those messages, this string is returned.
 */
public class AutoResponseListener extends ListenerAdapter {
  private final AutoResponseCache responseCache;
  
  /**
   * Creates a new instance of this class.
   *
   * @param responseCache The global cache of all automatic responses.
   */
  public AutoResponseListener(AutoResponseCache responseCache) {
    this.responseCache = responseCache;
  }
  
  /**
   * Checks for every guild message whether it matches one of the registered auto-responses. Replies
   * with the first valid, pre-defined answer on success.
   *
   * @param event The event containing the received guild messages.
   */
  @Override
  public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
    if (event.getAuthor().isBot()) {
      return;
    }
    
    responseCache.get(event.getGuild()).ifPresent(matcher -> {
      matcher.match(event.getMessage().getContentRaw()).ifPresent(response -> {
        event.getMessage().reply(response).queue();
      });
    });
  }
}
