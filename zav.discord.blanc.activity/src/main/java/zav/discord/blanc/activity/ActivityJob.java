package zav.discord.blanc.activity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.api.Guild;
import zav.discord.blanc.api.Job;
import zav.discord.blanc.api.Shard;

/**
 * This job is used to periodically update the activity for all guilds within a given shard.
 */
public class ActivityJob implements Job {
  private static final Logger LOGGER = LogManager.getLogger(ActivityJob.class);
  private final Shard shard;
  
  public ActivityJob(Shard shard) {
    this.shard = shard;
  }
  
  @Override
  public void run() {
    LOGGER.info("Update activity for shard {}.", shard);
    shard.getGuilds().forEach(Guild::updateActivity);
  }
}
