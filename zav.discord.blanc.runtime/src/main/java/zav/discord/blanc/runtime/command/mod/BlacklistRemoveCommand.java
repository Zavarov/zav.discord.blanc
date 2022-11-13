package zav.discord.blanc.runtime.command.mod;

import java.util.EnumSet;
import java.util.Set;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import zav.discord.blanc.api.cache.PatternCache;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;

/**
 * This command whitelists certain words. Any previously blacklisted word will no longer be deleted
 * by this application.
 */
public class BlacklistRemoveCommand extends AbstractGuildCommand {
  private final PatternCache cache;
  private final SlashCommandEvent event;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public BlacklistRemoveCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager);
    this.event = event;
    this.cache = manager.getShard().get(PatternCache.class);
  }

  @Override
  public void run() {
    GuildEntity entity = GuildEntity.find(event.getGuild());
    event.reply(modify(entity)).complete();
    entity.merge();
  }
  
  private String modify(GuildEntity entity) {
    OptionMapping pattern = event.getOption("pattern");
    OptionMapping index = event.getOption("index");
    
    if (pattern != null) {
      return removeByName(entity, pattern.getAsString());
    } else if (index != null) {
      return removeByIndex(entity, (int) index.getAsLong());
    }
    
    return getMessage("blacklist_invalid_argument");
  }
  
  private String removeByName(GuildEntity entity, String pattern) {
    if (entity.getBlacklist().remove(pattern)) {
      cache.invalidate(event.getGuild());
      
      return getMessage("blacklist_remove", pattern);
    }
    
    return getMessage("blacklist_name_not_found", pattern);
  }
  
  private String removeByIndex(GuildEntity entity, int index) {
    if (index >= 0 && index < entity.getBlacklist().size()) {
      return removeByName(entity, entity.getBlacklist().get(index));
    }
    
    return getMessage("blacklist_index_not_found", index);
  }
  
  @Override
  protected Set<Permission> getPermissions() {
    return EnumSet.of(Permission.MESSAGE_MANAGE);
  }

}
