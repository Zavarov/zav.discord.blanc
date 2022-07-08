package zav.discord.blanc.runtime.internal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.TextChannelEntity;

/**
 * This test case checks whether invalid text channels are detected properly.
 */
@SuppressWarnings("deprecation")
@ExtendWith(MockitoExtension.class)
public class TextChannelValidatorTest {
  @Mock Guild guild;
  @Mock TextChannel channel;
  TextChannelEntity entity;
  TextChannelValidator validator;
  
  @BeforeEach
  public void setUp() {
    entity = new TextChannelEntity();
    validator = new TextChannelValidator(guild);
  }
  
  @Test
  public void testUnknownChannel() {
    assertTrue(validator.test(entity));
  }
  
  @Test
  public void testInaccessible() {
    when(guild.getTextChannelById(anyLong())).thenReturn(channel);
    assertTrue(validator.test(entity));
  }
  
  @Test
  public void testValid() {
    when(guild.getTextChannelById(anyLong())).thenReturn(channel);
    when(channel.canTalk()).thenReturn(true);
    assertFalse(validator.test(entity));
  }

}
