package zav.discord.blanc.runtime.command.mod;

import java.util.regex.Pattern;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.apache.commons.lang3.StringUtils;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.AutoResponseEntity;
import zav.discord.blanc.databind.GuildEntity;

/**
 * This command allows the user to register automatic responses. The bot will respond to any message
 * matching the registered expressions with the pre-defined response.
 */
public class ResponseAddCommand extends AbstractResponseCommand {
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public ResponseAddCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(event, manager);
  }

  @Override
  protected String modify(GuildEntity entity, SlashCommandEvent event) {
    String regex = event.getOption("regex").getAsString();
    String answer = event.getOption("answer").getAsString();
    
    Pattern pattern = Pattern.compile(regex);
    if (pattern.matcher(StringUtils.EMPTY).groupCount() > 0) {
      return getMessage("response_groups_not_allowed");
    }
    
    AutoResponseEntity responseEntity = AutoResponseEntity.create(regex, answer);
    entity.add(responseEntity);

    // Remove the corresponding entry from cache
    client.getAutoResponseCache().invalidate(guild);

    return getMessage("response_added", regex, answer);
  }

}
