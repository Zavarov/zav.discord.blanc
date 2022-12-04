package zav.discord.blanc.runtime.command.core;

import java.util.EnumSet;
import java.util.Set;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.command.CommandManager;

/**
 * This command is used to generate an invitation link for this bot in case the "Add to Server"
 * button can't be used.
 */
public class InviteCommand extends AbstractCommand {
  
  private static final String PATTERN = "https://discord.com/oauth2/authorize?client_id=%s&scope=bot&permissions=%s";
  
  private static final Set<Permission> REQUIRED_PERMISSIONS = EnumSet.of(
      Permission.MESSAGE_READ,
      Permission.MESSAGE_WRITE,
      Permission.MESSAGE_MANAGE,
      Permission.MANAGE_WEBHOOKS
  );
  
  private final SlashCommandEvent event;
  
  public InviteCommand(SlashCommandEvent event, CommandManager manager) {
    super(manager);
    this.event = event;
  }

  @Override
  public void run() throws Exception {
    EmbedBuilder builder = new EmbedBuilder();
    builder.setDescription(getMessage("bot_invitation", getInviteUrlText(), getPermissionText()));
    
    event.replyEmbeds(builder.build()).complete();
    
  }
  
  private String getInviteUrlText() {
    return String.format(PATTERN, getClientId(), getRequiredPermissions());
  }
  
  private String getPermissionText() {
    return REQUIRED_PERMISSIONS.stream()
        .map(Permission::getName)
        .reduce((u, v) -> u + System.lineSeparator() + v)
        .orElseThrow();
  }
  
  private String getClientId() {
    return event.getJDA().getSelfUser().getId();
  }

  private String getRequiredPermissions() {
    return Long.toUnsignedString(Permission.getRaw(REQUIRED_PERMISSIONS));
  }
}
