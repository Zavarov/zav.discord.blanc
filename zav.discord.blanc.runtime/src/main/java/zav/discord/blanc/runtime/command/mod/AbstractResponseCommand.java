package zav.discord.blanc.runtime.command.mod;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;

/**
 * Abstract base class for all commands modifying a guilds auto-responses.
 */
public abstract class AbstractResponseCommand extends AbstractGuildCommand {
  protected final Client client;
  protected final Guild guild;
  private final EntityManagerFactory factory;
  private final SlashCommandEvent event;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  protected AbstractResponseCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager, Permission.MESSAGE_MANAGE);
    this.factory = manager.getClient().getEntityManagerFactory();
    this.event = event;
    this.guild = event.getGuild();
    this.client = manager.getClient();
  }

  protected abstract String modify(GuildEntity entity, SlashCommandEvent event);
  
  @Override
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  public void run() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity entity = GuildEntity.getOrCreate(entityManager, guild);
      
      final String response = modify(entity, event);
  
      // Write changes to the database
      entityManager.getTransaction().begin();
      entityManager.merge(entity);
      entityManager.getTransaction().commit();
      
      event.reply(response).complete();
    }
  }
}
