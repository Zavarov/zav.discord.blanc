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
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebHookEntity;
import zav.discord.blanc.db.TextChannelDatabaseTable;
import zav.discord.blanc.db.WebHookDatabaseTable;

/**
 * Executable class for updating all registered Subreddit feeds.
 */
public class RedditJob implements Runnable {
  private static final Logger LOGGER = LogManager.getLogger(RedditJob.class);
  
  @Inject
  private SubredditObservable observable;
  
  @Inject
  private TextChannelDatabaseTable textDb;
  
  @Inject
  private WebHookDatabaseTable hookDb;
  
  /*package*/ RedditJob() {
    // Instantiated with Guice
  }
  
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
        for (WebHookEntity webHook : hookDb.get(guild.getIdLong())) {
          loadWebHooks(guild, webHook);
        }
        for (TextChannelEntity textChannel : textDb.get(guild.getIdLong())) {
          loadTextChannels(guild, textChannel);
        }
      }
    }
  }
  
  private void loadWebHooks(Guild guild, WebHookEntity entity) throws SQLException {
    // Text channel may no longer exist...
    @Nullable TextChannel textChannel = guild.getTextChannelById(entity.getChannelId());
  
    if (textChannel == null) {
      hookDb.delete(entity.getGuildId(), entity.getChannelId(), entity.getId());
      LOGGER.error("TextChannel with id {} no longer exists -> delete...", entity.getChannelId());
      return;
    }
    
    List<Webhook> webhooks = textChannel.retrieveWebhooks().complete();
  
    // Web hook may no longer exist...
    @Nullable Webhook webhook = webhooks.stream()
          .filter(hook -> hook.getName().equals("Reddit"))
          .findFirst()
          .orElse(null);
    
    if (webhook == null) {
      hookDb.delete(entity.getGuildId(), entity.getChannelId(), entity.getId());
      LOGGER.error("Webhook with id {} no longer exists -> delete...", entity.getId());
      return;
    }
    
    for (String subreddit : entity.getSubreddits()) {
      LOGGER.info("Add subreddit '{}' to webhook '{}'.", subreddit, entity.getName());
      observable.addListener(subreddit, webhook);
    }
  }
  
  private void loadTextChannels(Guild guild, TextChannelEntity entity) throws SQLException {
    // Text channel may no longer exist...
    @Nullable TextChannel textChannel = guild.getTextChannelById(entity.getId());
  
    if (textChannel == null) {
      textDb.delete(entity.getGuildId(), entity.getId());
      LOGGER.error("TextChannel with id {} no longer exists -> delete...", entity.getId());
      return;
    }
    
    for (String subreddit : entity.getSubreddits()) {
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
