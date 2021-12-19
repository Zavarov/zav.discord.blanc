package zav.discord.blanc.command;

import java.util.Set;
import javax.inject.Inject;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.Guild;
import zav.discord.blanc.api.GuildMessage;
import zav.discord.blanc.api.Member;
import zav.discord.blanc.api.Permission;
import zav.discord.blanc.api.TextChannel;

/**
 * Base class for all guild commands.<br>
 * Guild commands may require additional Guild-specific permissions in order to be executed.
 */
public abstract class AbstractGuildCommand extends AbstractCommand {
  @Inject
  protected @Nullable Guild guild;
  @Inject
  protected @Nullable TextChannel channel;
  @Inject
  protected @Nullable Member author;
  @Inject
  protected @Nullable GuildMessage message;
  
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
