package zav.discord.blanc.runtime.command.mod;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.reddit.SubredditObservable;

/**
 * Abstract base class for either adding or removing a subreddit to a text channel.
 */
public abstract class AbstractRedditCommand extends AbstractGuildCommand {
  protected static final String WEBHOOK = "Reddit";
  protected final SubredditObservable reddit;
  protected final SlashCommandEvent event;
  protected final TextChannel channel;
  private final Client client;
  private final Shard shard;
  private final User self;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public AbstractRedditCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager);
    this.event = event;
    this.shard = manager.getShard();
    this.client = shard.getClient();
    this.reddit = client.get(SubredditObservable.class);
    this.channel = event.getTextChannel();
    this.self = event.getJDA().getSelfUser();
  }
  
  protected final Optional<Webhook> getWebhook() {
    return channel.retrieveWebhooks()
    .complete()
    .stream()
    .filter(e -> e.getOwner().getIdLong() == self.getIdLong())
    .findFirst();
  }
  
  protected final Webhook createWebhook() {
    return channel.createWebhook(WEBHOOK).complete();
  }
  
  @Override
  protected Set<Permission> getPermissions() {
    return EnumSet.of(Permission.MANAGE_CHANNEL);
  }
}