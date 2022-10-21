package zav.discord.blanc.runtime.command.mod;

import javax.inject.Inject;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;

/**
 * This command whitelists certain words. Any previously blacklisted word will no longer be deleted
 * by this application.
 */
public class BlacklistRemoveCommand extends AbstractBlacklistCommand {
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  @Inject
  public BlacklistRemoveCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(event, manager);
  }
  
  @Override
  protected String modify(GuildEntity entity, SlashCommandEvent event) {
    OptionMapping regex = event.getOption("regex");
    OptionMapping index = event.getOption("index");
    
    if (regex != null) {
      return removeByName(entity, regex.getAsString());
    } else if (index != null) {
      return removeByIndex(entity, (int) index.getAsLong());
    }
    
    return getMessage("blacklist_invalid_argument");
  }
  
  private String removeByName(GuildEntity entity, String regex) {
    if (entity.getBlacklist().remove(regex)) {
      return getMessage("blacklist_remove", regex);
    }
    
    return getMessage("blacklist_name_not_found", regex);
  }
  
  private String removeByIndex(GuildEntity entity, int index) {
    if (index >= 0 && index < entity.getBlacklist().size()) {
      return removeByName(entity, entity.getBlacklist().get(index));
    }
    
    return getMessage("blacklist_index_not_found", index);
  }

}
