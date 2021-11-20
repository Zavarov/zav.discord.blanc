package zav.discord.blanc.command;

import java.util.Set;
import javax.inject.Inject;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.Permission;
import zav.discord.blanc.Rank;
import zav.discord.blanc.view.GuildMessageView;
import zav.discord.blanc.view.GuildView;
import zav.discord.blanc.view.MemberView;
import zav.discord.blanc.view.TextChannelView;

/**
 * Base class for all guild commands.<br>
 * Guild commands may require additional Guild-specific permissions in order to be executed.
 */
public abstract class AbstractGuildCommand extends AbstractCommand {
  @Inject
  protected @Nullable GuildView guild;
  @Inject
  protected @Nullable TextChannelView channel;
  @Inject
  protected @Nullable MemberView author;
  @Inject
  protected @Nullable GuildMessageView message;
  
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
    
    assert author != null;
    
    // Does the user have the required permissions?
    if (!author.getPermissions().containsAll(permissions)) {
      throw new InsufficientPermissionException();
    }
  }
}
