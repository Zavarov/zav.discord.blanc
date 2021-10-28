package zav.discord.blanc.command;

import javax.inject.Inject;
import java.util.Set;
import zav.discord.blanc.Permission;
import zav.discord.blanc.Rank;
import zav.discord.blanc.view.GuildView;
import zav.discord.blanc.view.MemberView;
import zav.discord.blanc.view.TextChannelView;

public abstract class AbstractGuildCommand extends AbstractCommand {
  @Inject
  protected GuildView guild;
  @Inject
  protected TextChannelView channel;
  @Inject
  protected MemberView author;
  
  private final Set<Permission> permissions;
  
  protected AbstractGuildCommand(Permission... permissions) {
    this(Rank.USER, permissions);
  }
  
  protected AbstractGuildCommand(Rank rank) {
    this(rank, new Permission[0]);
  }
  
  protected AbstractGuildCommand(Rank rank, Permission... permissions) {
    super(rank);
    this.permissions = Set.of(permissions);
  }
  
  @Override
  public void validate() throws InvalidCommandException {
    super.validate();
    
    // Does the user have the required permissions?
    if (!author.getPermissions().containsAll(permissions)) {
      throw new InsufficientPermissionException();
    }
  }
}
