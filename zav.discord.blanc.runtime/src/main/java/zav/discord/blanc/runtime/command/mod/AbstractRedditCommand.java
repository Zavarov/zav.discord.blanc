package zav.discord.blanc.runtime.command.mod;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.EnumSet;
import java.util.Set;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.reddit.SubredditObservable;

/**
 * Abstract base class for either adding or removing a subreddit to a text channel.
 */
public abstract class AbstractRedditCommand extends AbstractGuildCommand {
  protected static final String WEBHOOK = "Reddit";
  protected final Client client;
  protected final SubredditObservable reddit;
  protected final TextChannel channel;
  protected final Webhook webhook;
  private final EntityManagerFactory factory;
  private final SlashCommandEvent event;
  private final Guild guild;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public AbstractRedditCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager);
    this.event = event;
    this.guild = event.getGuild();
    this.client = manager.getClient();
    this.reddit = client.getSubredditObservable();
    this.factory = client.getEntityManagerFactory();
    this.channel = event.getTextChannel();
    
    this.webhook = channel.retrieveWebhooks()
        .complete()
        .stream()
        .filter(e -> WEBHOOK.equals(e.getName()))
        .findFirst()
        .orElseGet(() -> channel.createWebhook(WEBHOOK).complete());
  }

  protected abstract String modify(WebhookEntity entity, SlashCommandEvent event);
  
  @Override
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  public void run() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity guildEntity = GuildEntity.getOrCreate(entityManager, guild);
      TextChannelEntity channelEntity = TextChannelEntity.getOrCreate(entityManager, channel);
      WebhookEntity webhookEntity = WebhookEntity.getOrCreate(entityManager, webhook);
      
      final String response = modify(webhookEntity, event);
      
      // Update bi-directional associations
      guildEntity.add(channelEntity);
      guildEntity.add(webhookEntity);
      channelEntity.add(webhookEntity);
  
      // Write changes to the database
      entityManager.getTransaction().begin();
      entityManager.merge(guildEntity);
      entityManager.merge(channelEntity);
      entityManager.merge(webhookEntity);
      entityManager.getTransaction().commit();
      
      event.reply(response).complete();
    }
  }
  
  @Override
  protected Set<Permission> getPermissions() {
    return EnumSet.of(Permission.MANAGE_CHANNEL);
  }
}