package zav.discord.blanc.runtime.command.mod;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
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
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public AbstractRedditCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager);
    this.event = event;
    this.reddit = manager.getClient().getSubredditObservable();
    this.channel = event.getTextChannel();
  }
  
  protected final Optional<Webhook> getWebhook() {
    return channel.retrieveWebhooks()
    .complete()
    .stream()
    .filter(e -> WEBHOOK.equals(e.getName()))
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