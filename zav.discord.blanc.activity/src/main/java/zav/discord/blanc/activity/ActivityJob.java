package zav.discord.blanc.activity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.job.Job;
import zav.discord.blanc.view.GuildView;
import zav.discord.blanc.view.ShardView;

/**
 * This job is used to periodically update the activity for all guilds within a given shard.
 */
public class ActivityJob implements Job {
  private static final Logger LOGGER = LogManager.getLogger(ActivityJob.class);
  private final ShardView shard;
  
  public ActivityJob(ShardView shard) {
    this.shard = shard;
  }
  
  @Override
  public void run() {
    LOGGER.info("Update activity for shard {}.", shard);
    shard.getGuilds().forEach(GuildView::updateActivity);
  }
}
