package zav.discord.blanc.runtime.command.mod;

import jakarta.persistence.EntityManager;
import java.util.EnumSet;
import java.util.Set;
import java.util.regex.Pattern;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.util.AutoResponseCache;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.AutoResponseEntity;
import zav.discord.blanc.databind.GuildEntity;

/**
 * This command allows the user to register automatic responses. The bot will respond to any message
 * matching the registered expressions with the pre-defined response.
 */
public class ResponseAddCommand extends AbstractDatabaseCommand {
  private static final Pattern NAMED_GROUP = Pattern.compile("(\\?<\\w+>.*)");
  private final AutoResponseCache cache;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public ResponseAddCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(event, manager);
    this.cache = client.getAutoResponseCache();
  }

  @Override
  protected String modify(EntityManager entityManager, GuildEntity entity) {
    String pattern = event.getOption("pattern").getAsString();
    String answer = event.getOption("answer").getAsString();
    
    if (NAMED_GROUP.matcher(pattern).find()) {
      return getMessage("response_groups_not_allowed");
    }
    
    // Check that the pattern is a valid regular expression
    Pattern.compile(pattern);
    
    AutoResponseEntity responseEntity = AutoResponseEntity.create(pattern, answer);
    entity.add(responseEntity);

    // Remove the corresponding entry from cache
    cache.invalidate(guild);

    return getMessage("response_added", pattern, answer);
  }
  
  @Override
  protected Set<Permission> getPermissions() {
    return EnumSet.of(Permission.MESSAGE_MANAGE);
  }

}
