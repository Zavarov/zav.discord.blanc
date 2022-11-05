package zav.discord.blanc.runtime.command.mod;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;

public abstract class AbstractDatabaseCommand extends AbstractGuildCommand {
  protected final SlashCommandEvent event;
  protected final Client client;
  protected final Guild guild;
  private final EntityManagerFactory factory;
  
  protected AbstractDatabaseCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(event, manager);
    this.event = event;
    this.guild = event.getGuild();
    this.client = manager.getClient();
    this.factory = client.getEntityManagerFactory();
  }

  /**
   * May either add or delete an regular expression from the blacklist.
   * Does nothing in case the command argument doesn't correspond to a pattern.
   *
   * @param entityManager The persistence context.
   * @param entity The entity corresponding to the guild this command is executed in.
   * @return A human-readable description of the performed action.
   */
  protected abstract String modify(EntityManager entityManager, GuildEntity entity);
  
  @Override
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  public void run() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      final GuildEntity entity = GuildEntity.getOrCreate(entityManager, guild);
      final String response = modify(entityManager, entity);
      
      // Write changes to the database
      entityManager.getTransaction().begin();
      entityManager.merge(entity);
      entityManager.getTransaction().commit();
      
      reply(response);
    }
  }

}
