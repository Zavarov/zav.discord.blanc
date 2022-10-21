package zav.discord.blanc.runtime.command.mod;

import static net.dv8tion.jda.api.Permission.MESSAGE_MANAGE;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.runtime.internal.PageUtils;

/**
 * Abstract base class for printing all registered subreddits of a text channel.
 * Used by both the deprecated command posting submissions to the text channels
 * directly, and the new approach using webhooks.
 */
public abstract class AbstractRedditInfoCommand extends AbstractGuildCommand {  
  private final EntityManagerFactory factory;
  private final GuildCommandManager manager;
  private final Client client;
  private final Guild guild;
  protected final TextChannel channel;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  @Inject
  public AbstractRedditInfoCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager, MESSAGE_MANAGE);
    this.guild = event.getGuild();
    this.channel = event.getTextChannel();
    this.manager = manager;
    this.client = manager.getClient();
    this.factory = client.getEntityManagerFactory();
  }
  
  public abstract List<String> getSubreddits(GuildEntity entity);

  @Override
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_OF_NONNULL_VALUE")
  public void run() {
    List<Site.Page> pages = new ArrayList<>();
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity guildEntity = GuildEntity.getOrCreate(entityManager, guild);
      
      pages.addAll(PageUtils.convert("Subreddit feeds", getSubreddits(guildEntity), 10));
    }
    
    manager.submit(pages);
  }

}
