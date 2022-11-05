package zav.discord.blanc.runtime.internal.validator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.databind.TextChannelEntity;

/**
 * This class checks whether the persisted text channels are still valid.<br>
 * Channels become invalid, if one of the following conditions are met:
 * <pre>
 *   - The channel has been deleted
 *   - The channel is no longer accessible by the program.
 * </pre>
 */
@Deprecated
public class TextChannelValidator implements Validator<TextChannelEntity> {
  private static final Logger LOGGER = LoggerFactory.getLogger(TextChannelValidator.class);
  private final Guild guild;
  
  /**
   * Creates a new instance of this class.
   *
   * @param guild The guild managed by this validator.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public TextChannelValidator(Guild guild) {
    this.guild = guild;
  }
  
  @Override
  public boolean test(TextChannelEntity entity) {
    TextChannel textChannel = guild.getTextChannelById(entity.getId());
    
    if (textChannel == null) {
      LOGGER.error("Invalid textchannel {0}: It doesn't exist.", entity.getName());
      return true;
    }
    
    if (!textChannel.canTalk()) {
      LOGGER.error("Invalid textchannel {0}: Inaccessible.", entity.getName());
      return true;
    }
    
    return false;
  }

}
