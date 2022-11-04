package zav.discord.blanc.api.listener;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.cache.AutoResponseCache;
import zav.discord.blanc.api.util.RegularExpressionMatcher;

/**
 * Test class for checking whether the bot automatically responds to matching regular expressions.
 */
@ExtendWith(MockitoExtension.class)
public class AutoResponseListenerTest {
  @Mock GuildMessageReceivedEvent event;
  @Mock RegularExpressionMatcher matcher;
  @Mock AutoResponseCache cache;
  @Mock MessageAction action;
  @Mock Message message;
  @Mock Guild guild;
  @Mock User author;
  AutoResponseListener listener;
  
  /**
   * Initializes the response listener.
   */
  @BeforeEach
  public void setUp() {
    when(event.getAuthor()).thenReturn(author);
    listener = new AutoResponseListener(cache);
  }
  
  /**
   * Use Case: Only respond to user messages.
   */
  @Test
  public void testIgnoreBot() {
    when(author.isBot()).thenReturn(true);
    
    listener.onGuildMessageReceived(event);

    verify(cache, times(0)).get(any());
  }
  
  /**
   * Use Case: Guild doesn't have any automatic responses.
   */
  @Test
  public void testIgnoreUnrelatedGuild() {
    when(event.getGuild()).thenReturn(guild);
    when(cache.get(guild)).thenReturn(Optional.empty());
    
    listener.onGuildMessageReceived(event);
    
    verify(cache).get(any());
    verify(message, times(0)).reply(anyString());
  }
  
  /**
   * Use Case: Message didn't match any pattern.
   */
  @Test
  public void testIgnoreUnrelatedMessage() {
    when(event.getGuild()).thenReturn(guild);
    when(event.getMessage()).thenReturn(message);
    when(cache.get(guild)).thenReturn(Optional.of(matcher));
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(matcher.match(anyString())).thenReturn(Optional.empty());
    
    listener.onGuildMessageReceived(event);
    
    verify(cache).get(any());
    verify(matcher).match(anyString());
    verify(message, times(0)).reply(anyString());
  }
  
  /**
   * Use Case: Message DID match one of the patterns.
   */
  @Test
  public void testRespondToMatch() {
    when(event.getGuild()).thenReturn(guild);
    when(event.getMessage()).thenReturn(message);
    when(cache.get(guild)).thenReturn(Optional.of(matcher));
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    when(message.reply(anyString())).thenReturn(action);
    when(matcher.match(anyString())).thenReturn(Optional.of(StringUtils.EMPTY));
    
    listener.onGuildMessageReceived(event);
    
    verify(cache).get(any());
    verify(matcher).match(anyString());
    verify(message).reply(anyString());
  }
}
