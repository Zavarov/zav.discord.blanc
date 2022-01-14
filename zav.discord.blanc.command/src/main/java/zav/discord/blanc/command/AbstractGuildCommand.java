package zav.discord.blanc.command;

import java.util.Set;
import javax.inject.Inject;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Rank;

/**
 * Base class for all guild commands.<br>
 * Guild commands may require additional Guild-specific permissions in order to be executed.
 */
public abstract class AbstractGuildCommand extends AbstractCommand {
  @Inject
  protected Guild guild;
  @Inject
  protected TextChannel channel;
  @Inject
  protected Member author;
  @Inject
  protected Message message;
  
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
  @Contract(pure = true)
  public void validate() throws Exception {
    super.validate();
    
    // Does the user have the required permissions?
    if (!author.getPermissions().containsAll(permissions)) {
      throw new InsufficientPermissionException();
    }
  }
}
