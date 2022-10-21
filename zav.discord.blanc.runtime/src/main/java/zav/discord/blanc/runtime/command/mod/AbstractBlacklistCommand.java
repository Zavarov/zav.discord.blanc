package zav.discord.blanc.runtime.command.mod;

import static net.dv8tion.jda.api.Permission.MESSAGE_MANAGE;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.PatternCache;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;

/**
 * Abstract base class for all commands modifying a guilds blacklist.
 */
public abstract class AbstractBlacklistCommand  extends AbstractGuildCommand {
  private final EntityManagerFactory factory;
  private final SlashCommandEvent event;
  private final PatternCache cache;
  private final Client client;
  private final Guild guild;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  @Inject
  public AbstractBlacklistCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager, MESSAGE_MANAGE);
    this.event = event;
    this.guild = event.getGuild();
    this.client = manager.getClient();
    this.cache = client.getPatternCache();
    this.factory = client.getEntityManagerFactory();
  }
  
  /**
   * May either add or delete an regular expression from the blacklist.
   * Does nothing in case the given argument doesn't correspond to any pattern.
   *
   * @param entity The entity corresponding to the guild this command is executed in.
   * @param event The event that triggered this command.
   * @return A human-readable description of the performed action.
   */
  protected abstract String modify(GuildEntity entity, SlashCommandEvent event);
  
  @Override
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  public void run() {    
    try (EntityManager entityManager = factory.createEntityManager()) {
      final GuildEntity entity = GuildEntity.getOrCreate(entityManager, guild);
      final String response = modify(entity, event);

      // Write changes to the database
      entityManager.getTransaction().begin();
      entityManager.merge(entity);
      entityManager.getTransaction().commit();
      
      cache.invalidate(guild);
      event.reply(MarkdownSanitizer.escape(response)).complete();
    }
  }
}
