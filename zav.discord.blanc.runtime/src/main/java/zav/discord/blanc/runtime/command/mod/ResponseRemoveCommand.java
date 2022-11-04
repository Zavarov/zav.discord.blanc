package zav.discord.blanc.runtime.command.mod;

import jakarta.persistence.EntityManager;
import java.util.EnumSet;
import java.util.Set;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.cache.AutoResponseCache;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.AutoResponseEntity;
import zav.discord.blanc.databind.GuildEntity;

/**
 * This command allows the user to remove automatic responses. The responses are identified by their
 * (fixed) index in the database.
 */
public class ResponseRemoveCommand extends AbstractDatabaseCommand {
  private final AutoResponseCache cache;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public ResponseRemoveCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(event, manager);
    this.cache = client.getAutoResponseCache();
  }

  @Override
  protected String modify(EntityManager entityManager, GuildEntity entity) {
    int index = (int) event.getOption("index").getAsLong();
    
    if (index < 0 || index >= entity.getAutoResponses().size()) {
      return getMessage("response_index_not_found", index);
    }
    
    AutoResponseEntity responseEntity = entity.getAutoResponses().get(index);
    entity.remove(responseEntity);
    
    // Remove the corresponding entry from cache
    cache.invalidate(guild);
    
    return getMessage("response_removed", responseEntity.getPattern());
  }
  
  @Override
  protected Set<Permission> getPermissions() {
    return EnumSet.of(Permission.MESSAGE_MANAGE);
  }

}
