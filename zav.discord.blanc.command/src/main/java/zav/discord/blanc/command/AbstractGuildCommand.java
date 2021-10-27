package zav.discord.blanc.command;

import javax.inject.Inject;
import java.util.Set;
import zav.discord.blanc.Permission;
import zav.discord.blanc.Rank;
import zav.discord.blanc.view.GuildView;
import zav.discord.blanc.view.MemberView;
import zav.discord.blanc.view.ShardView;
import zav.discord.blanc.view.TextChannelView;

public abstract class AbstractGuildCommand implements Command {
  @Inject
  protected ShardView shard;
  @Inject
  protected GuildView guild;
  @Inject
  protected TextChannelView channel;
  @Inject
  protected MemberView author;
  
  private final Set<Permission> permissions;
  private final Rank rank;
  
  protected AbstractGuildCommand(Permission... permissions) {
    this(Rank.USER, permissions);
  }
  
  protected AbstractGuildCommand(Rank rank) {
    this(rank, new Permission[0]);
  }
  
  protected AbstractGuildCommand(Rank rank, Permission... permissions) {
    this.permissions = Set.of(permissions);
    this.rank = rank;
  }
  
  @Override
  public void validate() throws InvalidCommandException {
    // Does the user have the required rank?
    if (!author.getRanks().contains(rank)) {
      throw new InsufficientRankException();
    }
    
    // Does the user have the required permissions?
    if (!permissions.containsAll(author.getPermissions())) {
      throw new InsufficientPermissionException();
    }
  }
}
