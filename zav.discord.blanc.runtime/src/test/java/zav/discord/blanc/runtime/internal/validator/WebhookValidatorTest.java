package zav.discord.blanc.runtime.internal.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * This test case checks whether invalid webhooks are detected properly.
 */
@ExtendWith(MockitoExtension.class)
public class WebhookValidatorTest extends AbstractTest {
  WebhookValidator validator;
  
  /**
   * Initializes the validator with a mocked webhook entity.
   */
  @BeforeEach
  public void setUp() {
    validator = new WebhookValidator(guild);
  }
  
  @Test
  public void testUnknownChannel() {
    when(guild.getTextChannelById(anyLong())).thenReturn(null);
    assertTrue(validator.test(webhookEntity));
  }
  
  @Test
  public void testUnknownWebhook() {
    // No other webhooks
    when(channel.canTalk()).thenReturn(true);
    when(selfMember.hasPermission(any(TextChannel.class), any(Permission.class))).thenReturn(true);
    when(retrieveWebhooks.complete()).thenReturn(Collections.emptyList());
    assertTrue(validator.test(webhookEntity));
    // A single webhook with different id
    when(webhook.getIdLong()).thenReturn(Long.MAX_VALUE);
    when(retrieveWebhooks.complete()).thenReturn(List.of(webhook));
    assertTrue(validator.test(webhookEntity));
  }
  
  @Test
  public void testInsufficientPermission() {
    when(channel.canTalk()).thenReturn(true);
    when(selfMember.hasPermission(any(TextChannel.class), any(Permission.class))).thenReturn(false);
    assertTrue(validator.test(webhookEntity));
  }
  
  @Test
  public void testInaccessible() {
    when(guild.getTextChannelById(anyLong())).thenReturn(channel);
    assertTrue(validator.test(webhookEntity));
  }
  
  @Test
  public void testValid() {
    when(channel.canTalk()).thenReturn(true);
    when(selfMember.hasPermission(any(TextChannel.class), any(Permission.class))).thenReturn(true);
    assertFalse(validator.test(webhookEntity));
  }
}
