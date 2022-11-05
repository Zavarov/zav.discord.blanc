package zav.discord.blanc.runtime.command.mod;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.RichResponse;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.AutoResponseEntity;
import zav.discord.blanc.databind.GuildEntity;

/**
 * This command displays all currently registered auto-responses.
 */
public class ResponseInfoCommand extends AbstractGuildCommand implements RichResponse {
  
  private final EntityManagerFactory factory;
  private final GuildCommandManager manager;
  private final Client client;
  private final Guild guild;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public ResponseInfoCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(event, manager);
    this.guild = event.getGuild();
    this.manager = manager;
    this.client = manager.getClient();
    this.factory = client.getEntityManagerFactory();
  }

  @Override
  public void run() {
    manager.submit(getPages());
  }
  
  @Override
  public List<Site.Page> getPages() {
    Site.Page.Builder builder = new Site.Page.Builder();
    builder.setItemsPerPage(5);
    builder.setLabel("Automatic Responses");
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity entity = GuildEntity.getOrCreate(entityManager, guild);
      
      List<AutoResponseEntity> responses = entity.getAutoResponses();
      for (int i = 0; i < responses.size(); ++i) {
        String pattern = MarkdownSanitizer.escape(responses.get(i).getPattern());
        String answer = MarkdownSanitizer.escape(responses.get(i).getAnswer());
        builder.add("`[{0}]` {1}\n → _{2}_\n", i, pattern, answer);
      }
    }

    return builder.build();
  }
  
  @Override
  protected Set<Permission> getPermissions() {
    return EnumSet.of(Permission.MESSAGE_MANAGE);
  }
}
