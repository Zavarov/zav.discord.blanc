package zav.discord.blanc.runtime.command.mod;

import static net.dv8tion.jda.api.Permission.MESSAGE_MANAGE;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.AutoResponseEntity;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.runtime.internal.PageUtils;

/**
 * This command displays all currently registered auto-responses.
 */
public class ResponseInfoCommand extends AbstractGuildCommand {
  
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
    super(manager, MESSAGE_MANAGE);
    this.guild = event.getGuild();
    this.manager = manager;
    this.client = manager.getClient();
    this.factory = client.getEntityManagerFactory();
  }

  @Override
  public void run() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity entity = GuildEntity.getOrCreate(entityManager, guild);

      List<Site.Page> pages = PageUtils.convert("Automatic Responses", convert(entity.getAutoResponses()), 10);

      manager.submit(pages);
    }
  }
  
  private List<String> convert(List<AutoResponseEntity> source) {
    List<String> result = new ArrayList<>();
    
    for (AutoResponseEntity entity : source) {
      result.add(MessageFormat.format("`{0}`%n{1}", entity.getExpression(), entity.getAnswer()));
    }
    
    return Collections.unmodifiableList(result);
  }
}
