package zav.discord.blanc.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.api.cache.SiteCache;
import zav.discord.blanc.command.internal.PermissionValidator;

/**
 * This test case checks whether the utility functions provided by the guild command manager are
 * functional.
 */
@ExtendWith(MockitoExtension.class)
public class GuildCommandManagerTest {
  @Captor ArgumentCaptor<String> captor;
  @Mock Message message;
  @Mock RestAction<Message> response;
  @Mock InsufficientPermissionException exception;
  @Mock Shard shard;
  @Mock SlashCommandEvent event;
  @Mock Member member;
  @Mock ReplyAction action;
  @Mock InteractionHook hook;
  @Mock MessageEmbed entry;
  @Mock SiteCache cache;
  
  Site.Page page;
  GuildCommandManager manager;
  
  /**
   * Initializes the GuildCommandManager instance with a mocked permission validator.
   */
  @BeforeEach
  public void setUp() {
    try (var mocked = mockConstruction(PermissionValidator.class)) {
      when(event.getMember()).thenReturn(member);
      manager = new GuildCommandManager(shard, event);
      page = Site.Page.create(entry);
    }
  }
  
  @Test
  public void testValidate() throws InsufficientPermissionException {
    // PermissionValidator has been mocked, hence the call should proceed without any error
    manager.validate(List.of(Permission.ADMINISTRATOR));
  }
  
  @Test
  public void testSubmitEmpty() {
    when(event.reply(captor.capture())).thenReturn(action);
    
    manager.submit(Collections.emptyList(), "site");
    
    assertEquals(captor.getValue(), "No entries.");
  }
  
  @Test
  public void testSubmit() {
    when(event.deferReply()).thenReturn(action);
    when(action.addEmbeds(any(MessageEmbed.class))).thenReturn(action);
    when(action.complete()).thenReturn(hook);
    when(hook.retrieveOriginal()).thenReturn(response);
    when(response.complete()).thenReturn(message);
    when(shard.get(SiteCache.class)).thenReturn(cache);
    
    manager.submit(List.of(page), "site");

    verify(action, times(0)).addActionRows(any(ActionRow.class));
    verify(cache).put(any(), any());
  }
  
  @Test
  public void testSubmitWithArrows() {
    when(event.deferReply()).thenReturn(action);
    when(action.addEmbeds(any(MessageEmbed.class))).thenReturn(action);
    when(action.addActionRows(any(ActionRow.class))).thenReturn(action);
    when(action.complete()).thenReturn(hook);
    when(hook.retrieveOriginal()).thenReturn(response);
    when(response.complete()).thenReturn(message);
    when(shard.get(SiteCache.class)).thenReturn(cache);
    
    manager.submit(List.of(page, page), "site");

    verify(action).addActionRows(any(ActionRow.class));
    verify(cache).put(any(), any());
  }
}
