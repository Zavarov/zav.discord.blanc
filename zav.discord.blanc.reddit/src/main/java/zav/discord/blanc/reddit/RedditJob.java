package zav.discord.blanc.reddit;

import com.google.inject.Inject;
import java.sql.SQLException;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.databind.TextChannelDto;
import zav.discord.blanc.databind.WebHookDto;
import zav.discord.blanc.db.TextChannelDatabase;
import zav.discord.blanc.db.WebHookDatabase;

/**
 * Executable class for updating all registered Subreddit feeds.
 */
public class RedditJob implements Runnable {
  private static final Logger LOGGER = LogManager.getLogger(RedditJob.class);
  
  @Inject
  private SubredditObservable observable;
  
  /**
   * Creates listener for all text channel and webhooks that have been stored in the database.<br>
   * If a text channel/webhook no longer exists, they will be automatically removed from the
   * database.
   *
   * @param client The application client over all shards.
   * @throws SQLException If an SQL request to the database failed.
   */
  @Inject
  private void load(Client client) throws SQLException {
    for (JDA shard : client.getShards()) {
      for (Guild guild : shard.getGuilds()) {
        for (WebHookDto webHook : WebHookDatabase.getAll(guild.getIdLong())) {
          loadWebHooks(guild, webHook);
        }
        for (TextChannelDto textChannel : TextChannelDatabase.getAll(guild.getIdLong())) {
          loadTextChannels(guild, textChannel);
        }
      }
    }
  }
  
  private void loadWebHooks(Guild guild, WebHookDto dto) throws SQLException {
    // Text channel may no longer exist...
    @Nullable TextChannel textChannel = guild.getTextChannelById(dto.getChannelId());
  
    if (textChannel == null) {
      WebHookDatabase.delete(guild.getIdLong(), dto.getChannelId(), dto.getId());
      LOGGER.error("TextChannel with id {} no longer exists -> delete...", dto.getChannelId());
      return;
    }
    
    List<Webhook> webhooks = textChannel.retrieveWebhooks().complete();
  
    // Web hook may no longer exist...
    @Nullable Webhook webhook = webhooks.stream()
          .filter(hook -> hook.getName().equals("Reddit"))
          .findFirst()
          .orElse(null);
    
    if (webhook == null) {
      WebHookDatabase.delete(guild.getIdLong(), dto.getChannelId(), dto.getId());
      LOGGER.error("Webhook with id {} no longer exists -> delete...", dto.getId());
      return;
    }
    
    for (String subreddit : dto.getSubreddits()) {
      LOGGER.info("Add subreddit '{}' to webhook '{}'.", subreddit, dto.getName());
      observable.addListener(subreddit, webhook);
    }
  }
  
  private void loadTextChannels(Guild guild, TextChannelDto dto) throws SQLException {
    // Text channel may no longer exist...
    @Nullable TextChannel textChannel = guild.getTextChannelById(dto.getId());
  
    if (textChannel == null) {
      TextChannelDatabase.delete(guild.getIdLong(), dto.getId());
      LOGGER.error("TextChannel with id {} no longer exists -> delete...", dto.getId());
      return;
    }
    
    for (String subreddit : dto.getSubreddits()) {
      observable.addListener(subreddit, textChannel);
      LOGGER.info("Add subreddit '{}' to textChannel '{}'.", subreddit, textChannel.getName());
    }
  }

  @Override
  public void run() {
    try {
      LOGGER.info("Update Reddit feed.");
      observable.notifyAllObservers();
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
