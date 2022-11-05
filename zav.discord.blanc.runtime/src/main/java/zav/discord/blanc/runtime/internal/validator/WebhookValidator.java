package zav.discord.blanc.runtime.internal.validator;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.databind.WebhookEntity;

/**
 * This class checks whether the persisted webhooks are still valid.<br>
 * Webhooks become invalid, if one of the following conditions are met:
 * <pre>
 *   - The channel hosting the webhook has been deleted
 *   - The channel hosting the webhook is no longer accessible by the program.
 * </pre>
 */
public class WebhookValidator implements Validator<WebhookEntity> {
  private static final Logger LOGGER = LoggerFactory.getLogger(WebhookValidator.class);
  private final Guild guild;
  
  /**
   * Creates a new instance of this class.
   *
   * @param guild The guild managed by this validator.
   */
  @SuppressFBWarnings(value = "EI_EXPOSE_REP2")
  public WebhookValidator(Guild guild) {
    this.guild = guild;
  }
  
  @Override
  public boolean test(WebhookEntity entity) {
    TextChannel textChannel = guild.getTextChannelById(entity.getChannel().getId());
    
    if (textChannel == null) {
      LOGGER.error("Invalid textchannel {0}: It doesn't exist.", entity.getName());
      return true;
    }
    
    if (!textChannel.canTalk()) {
      LOGGER.error("Invalid textchannel {0}: Inaccessible.", entity.getName());
      return true;
    }
    
    Member self = textChannel.getGuild().getSelfMember();
    if (!self.hasPermission(textChannel, Permission.MANAGE_WEBHOOKS)) {
      LOGGER.error("Invalid textchannel {0}: Insufficient Permission.", entity.getName());
      return true;
    }
    
    List<Webhook> webhooks = textChannel.retrieveWebhooks().complete();
    if (webhooks.stream().noneMatch(webhook -> webhook.getIdLong() == entity.getId())) {
      LOGGER.error("Invalid webhook {0} : It doesn't exist.", entity.getName());
      return true;
    }
    
    return false;
  }
}
