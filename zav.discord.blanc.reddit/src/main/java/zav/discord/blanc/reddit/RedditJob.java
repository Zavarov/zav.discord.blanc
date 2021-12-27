package zav.discord.blanc.reddit;

import java.sql.SQLException;
import java.util.NoSuchElementException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Guild;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.api.TextChannel;
import zav.discord.blanc.api.WebHook;
import zav.discord.blanc.databind.TextChannelValueObject;
import zav.discord.blanc.databind.WebHookValueObject;
import zav.discord.blanc.db.TextChannelDatabase;
import zav.discord.blanc.db.WebHookDatabase;
import zav.discord.blanc.reddit.internal.ArgumentImpl;


/**
 * Executable class for updating all registered Subreddit feeds.
 */
public class RedditJob implements Runnable {
  private static final Logger LOGGER = LogManager.getLogger(RedditJob.class);
  
  public RedditJob(Client client) throws SQLException {
    for (Shard shard : client.getShards()) {
      for (Guild guild : shard.getGuilds()) {
        for (WebHookValueObject webHook : WebHookDatabase.getAll(guild.getAbout().getId())) {
          loadWebHooks(guild, webHook);
        }
        for (TextChannelValueObject textChannel : TextChannelDatabase.getAll(guild.getAbout().getId())) {
          loadTextChannels(guild, textChannel);
        }
      }
    }
  }
  
  private void loadWebHooks(Guild guild, WebHookValueObject webHook) throws SQLException {
    Argument argChannel = new ArgumentImpl(webHook.getChannelId());
    
    TextChannel textChannel;
    
    // Text channel may no longer exist...
    try {
      textChannel = guild.getTextChannel(argChannel);
    } catch (NoSuchElementException e) {
      // Text Channel has been deleted
      LOGGER.error(e.getMessage(), e);
      WebHookDatabase.delete(guild.getAbout().getId(), webHook.getChannelId(), webHook.getId());
      return;
    }
  
    // Web hook may no longer exist...
    try {
      WebHook entry = textChannel.getWebHook(webHook.getName(), false);
      
      for (String subreddit : webHook.getSubreddits()) {
        LOGGER.info("Add subreddit '{}' to webhook '{}'.", subreddit, webHook.getName());
        SubredditObservable.addListener(subreddit, entry);
      }
    } catch (NoSuchElementException e) {
      // Web Hook has been deleted
      WebHookDatabase.delete(guild.getAbout().getId(), webHook.getChannelId(), webHook.getId());
      LOGGER.error(e.getMessage(), e);
    }
  }
  
  private void loadTextChannels(Guild guild, TextChannelValueObject textChannel) throws SQLException {
    Argument argChannel = new ArgumentImpl(textChannel.getId());
  
    // Text channel may no longer exist...
    try {
      TextChannel entry = guild.getTextChannel(argChannel);
  
      for (String subreddit : textChannel.getSubreddits()) {
        SubredditObservable.addListener(subreddit, entry);
        LOGGER.info("Add subreddit '{}' to textChannel '{}'.", subreddit, textChannel.getName());
      }
    } catch (NoSuchElementException e) {
      // Text Channel has been deleted
      LOGGER.error(e.getMessage(), e);
      TextChannelDatabase.delete(guild.getAbout().getId(), textChannel.getId());
    }
  }

  @Override
  public void run() {
    try {
      LOGGER.info("Update Reddit feed.");
      SubredditObservable.notifyAllObservers();
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
