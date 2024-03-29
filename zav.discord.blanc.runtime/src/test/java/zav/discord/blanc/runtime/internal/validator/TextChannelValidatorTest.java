package zav.discord.blanc.runtime.internal.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * This test case checks whether invalid text channels are detected properly.
 */
@SuppressWarnings("deprecation")
@ExtendWith(MockitoExtension.class)
public class TextChannelValidatorTest extends AbstractTest {
  TextChannelValidator validator;
  
  @BeforeEach
  public void setUp() {
    validator = new TextChannelValidator(guild);
  }
  
  @Test
  public void testUnknownChannel() {
    when(guild.getTextChannelById(anyLong())).thenReturn(null);
    assertTrue(validator.test(channelEntity));
  }
  
  @Test
  public void testInaccessible() {
    when(guild.getTextChannelById(anyLong())).thenReturn(channel);
    assertTrue(validator.test(channelEntity));
  }
  
  @Test
  public void testValid() {
    when(guild.getTextChannelById(anyLong())).thenReturn(channel);
    when(channel.canTalk()).thenReturn(true);
    assertFalse(validator.test(channelEntity));
  }

}
