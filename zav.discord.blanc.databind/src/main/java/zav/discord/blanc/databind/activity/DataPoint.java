package zav.discord.blanc.databind.activity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.jdt.annotation.NonNull;
import zav.discord.blanc.databind.TextChannel;

/**
 * The activity within a guild describes both the amount of members and their participation.<br>
 * Throughout the lifetime of the guild, the total amount of members and its subset that are online
 * are counted periodically. Additionally, it also keeps track of the messages sent in the
 * individual text channels. The activity in those is then computed by taking all messages that have
 * been received within a specific interval and then dividing it by the duration to get the number
 * of messages per minute.
 */
@NonNull
public class DataPoint extends DataPointTOP {
  /**
   * This map keeps track of all messages that have been received in the individual text channels.
   * In order to minimize the overhead, we only keep track of the message occurrences and not
   * their content.<br>
   * The map has to be cleared at the start of every new period to avoid data to carry over.
   */
  @JsonIgnore
  @NonNull
  protected final Map<TextChannel, Long> channelActivity = new ConcurrentHashMap<>();
  
  /**
   * Returns an immutable map of the number of message per text channel.
   *
   * @return A map containing the number of messages per text channel.
   */
  @NonNull
  public Map<TextChannel, Long> getChannelActivity() {
    return Map.copyOf(channelActivity);
  }
}
