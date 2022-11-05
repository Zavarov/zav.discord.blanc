package zav.discord.blanc.runtime.command.mod;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.RichResponse;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;

/**
 * Abstract base class for printing all registered subreddits of a text channel.
 * Used by both the deprecated command posting submissions to the text channels
 * directly, and the new approach using webhooks.
 */
public abstract class AbstractRedditInfoCommand extends AbstractGuildCommand implements RichResponse {
  private final GuildCommandManager manager;
  private final Guild guild;
  protected final TextChannel channel;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public AbstractRedditInfoCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager);
    this.guild = event.getGuild();
    this.channel = event.getTextChannel();
    this.manager = manager;
  }
  
  public abstract List<String> getSubreddits(GuildEntity entity);

  @Override
  public void run() {
    manager.submit(getPages());
  }
  
  @Override
  public List<Site.Page> getPages() {
    Site.Page.Builder builder = new Site.Page.Builder();
    builder.setItemsPerPage(10);
    builder.setLabel("Subreddit Feeds");
    
    GuildEntity entity = GuildEntity.find(guild);

    List<String> subreddits = getSubreddits(entity);
    for (int i = 0; i < subreddits.size(); ++i) {
      builder.add("`[{0}]` r/{1}\n", i, subreddits.get(i));
    }
    
    return builder.build();
  }
  
  @Override
  protected Set<Permission> getPermissions() {
    return EnumSet.of(Permission.MANAGE_CHANNEL);
  }

}
