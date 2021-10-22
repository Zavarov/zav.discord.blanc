package zav.discord.blanc.databind.activity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.eclipse.jdt.annotation.NonNull;
import zav.discord.blanc.databind.TextChannel;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The activity within a guild describes both the amount of members and their participation.<br>
 * Throughout the lifetime of the guild, the total amount of members and its subset that are online
 * are counted periodically. Additionally, it also keeps track of the messages sent in the
 * individual text channels. The activity in those is then computed by taking all messages that have
 * been received within a specific interval and then dividing it by the duration to get the number
 * of messages per minute.
 */
public class DataPoint extends DataPointTOP {
  /**
   * The time in minutes between each update of the guild activity.
   */
  @JsonIgnore
  public static final Duration ACTIVITY_RATE = Duration.ofMinutes(30);
  /**
   * This map keeps track of all messages that have been received in the individual text channels.
   * In order to minimize the overhead, we only keep track of the message occurrences and not
   * their content.<br>
   * The map has to be cleared at the start of every new period to avoid data to carry over.
   */
  @NonNull
  @JsonIgnore
  protected final Map<TextChannel, Long> channelActivity = new ConcurrentHashMap<>();
  
  public Map<TextChannel, Long> getChannelActivity() {
    return Map.copyOf(channelActivity);
  }
  
  /**
   * Increases the count for the number of messages in the associated {@link TextChannel} by one.
   * @param channel The {@link TextChannel} in which the new message was received.
   */
  public void countMessage(@NonNull TextChannel channel){
    channelActivity.merge(channel, 1L, Long::sum);
  }
}
