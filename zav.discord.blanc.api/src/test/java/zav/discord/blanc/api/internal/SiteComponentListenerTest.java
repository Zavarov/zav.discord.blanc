/*
 * Copyright (c) 2022 Zavarov.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package zav.discord.blanc.api.internal;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static zav.discord.blanc.api.Constants.SITE;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import java.util.List;
import java.util.function.Consumer;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonInteraction;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenuInteraction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import net.dv8tion.jda.api.requests.restaction.interactions.UpdateInteractionAction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.api.Site;

/**
 * Checks whether site interactions are processed correctly.
 */
public class SiteComponentListenerTest {
  SiteComponentListener listener;
  ButtonClickEvent clickEvent;
  SelectionMenuEvent selectionEvent;
  Cache<Message, Site> cache = CacheBuilder.newBuilder().build();
  
  final String left = "left";
  final String right = "right";
  final long responseNumber = 11111L;
  
  @Mock JDA jda;
  @Mock User user;
  @Mock ButtonInteraction buttonInteraction;
  @Mock SelectionMenuInteraction selectionInteraction;
  @Mock Site site;
  @Mock Message message;
  @Mock Button button;
  @Mock ReplyAction replyAction;
  @Mock UpdateInteractionAction updateAction;
  @Mock MessageEmbed messageEmbed;
  
  AutoCloseable closeable;
  
  /**
   * Initialize a site and fictitious Button and SelectionMenu interactions which are used to
   * modify the page.
   */
  @BeforeEach
  public void setUp() {
    closeable = openMocks(this);
    
    // Execute moveLeft(...) / moveRight(...) / changeSelection(...) calls
    doAnswer(invocation -> {
      Consumer<MessageEmbed> response = invocation.getArgument(0);
      response.accept(messageEmbed);
      return null;
    }).when(site).moveLeft();
    
    doAnswer(invocation -> {
      Consumer<MessageEmbed> response = invocation.getArgument(0);
      response.accept(messageEmbed);
      return null;
    }).when(site).moveRight();
  
    doAnswer(invocation -> {
      Consumer<MessageEmbed> response = invocation.getArgument(1);
      response.accept(messageEmbed);
      return null;
    }).when(site).changeSelection(any());
    
    when(site.getOwner()).thenReturn(user);
    
    // Mock JDA instructions
    when(buttonInteraction.getMessage()).thenReturn(message);
    when(buttonInteraction.getButton()).thenReturn(button);
    when(buttonInteraction.deferEdit()).thenReturn(updateAction);
    when(buttonInteraction.deferReply()).thenReturn(replyAction);
    when(buttonInteraction.getUser()).thenReturn(user);
    
    when(selectionInteraction.getMessage()).thenReturn(message);
    when(selectionInteraction.getValues()).thenReturn(List.of(EMPTY));
    when(selectionInteraction.deferReply()).thenReturn(replyAction);
    when(selectionInteraction.deferEdit()).thenReturn(updateAction);
    when(selectionInteraction.getUser()).thenReturn(user);
  
    when(updateAction.setEmbeds(any(MessageEmbed.class))).thenReturn(updateAction);
    
    Injector injector = Guice.createInjector(new TestModule());
    
    clickEvent = new ButtonClickEvent(jda, responseNumber, buttonInteraction);
    selectionEvent = new SelectionMenuEvent(jda, responseNumber, selectionInteraction);
    listener = injector.getInstance(SiteComponentListener.class);
    cache.put(message, site);
  }
  
  @AfterEach
  public void tearDown() throws Exception {
    closeable.close();
  }
  
  @Test
  public void testIgnoreClickOnUnknownMessage() {
    // Interaction on unknown message
    when(buttonInteraction.getMessage()).thenReturn(mock(Message.class));
    
    listener.onButtonClick(clickEvent);
  
    verify(site, times(0)).moveLeft();
    verify(site, times(0)).moveRight();
  }
  
  @Test
  public void testIgnoreClickOnEphemeralMessage() {
    // Null for ephemeral message
    when(buttonInteraction.getButton()).thenReturn(null);
    
    listener.onButtonClick(clickEvent);
  
    verify(site, times(0)).moveLeft();
    verify(site, times(0)).moveRight();
  }
  
  @Test
  public void testIgnoreClickOnUnknownId() {
    // Null for unknown button ids
    when(button.getId()).thenReturn(null);
    
    listener.onButtonClick(clickEvent);
  
    verify(site, times(0)).moveLeft();
    verify(site, times(0)).moveRight();
  }
  
  @Test
  public void testClickLeft() {
    when(button.getId()).thenReturn(left);
  
    listener.onButtonClick(clickEvent);
  
    verify(site, times(1)).moveLeft();
    verify(site, times(0)).moveRight();
  }
  
  @Test
  public void testClickRight() {
    when(button.getId()).thenReturn(right);
  
    listener.onButtonClick(clickEvent);
  
    verify(site, times(0)).moveLeft();
    verify(site, times(1)).moveRight();
  }
  
  @Test
  public void testClickInvalidId() {
    when(button.getId()).thenReturn(EMPTY);
  
    listener.onButtonClick(clickEvent);
  
    verify(site, times(0)).moveLeft();
    verify(site, times(0)).moveRight();
  }
  
  @Test
  public void testSelectOnUnknownMessage() {
    // Interaction on unknown message
    when(selectionEvent.getMessage()).thenReturn(mock(Message.class));
  
    listener.onSelectionMenu(selectionEvent);
  
    verify(site, times(0)).moveLeft();
    verify(site, times(0)).moveRight();
  }
  
  @Test
  public void testSelectOnMultipleEntries() {
    // Interaction with two selected items
    when(selectionInteraction.getValues()).thenReturn(List.of(EMPTY, EMPTY));
  
    listener.onSelectionMenu(selectionEvent);
  
    verify(site, times(0)).moveLeft();
    verify(site, times(0)).moveRight();
  }
  
  @Test
  public void testChangeSelection() {
    listener.onSelectionMenu(selectionEvent);
  
    verify(site, times(1)).changeSelection(any());
  }
  
  @Test
  public void testIgnoreButtonByUnknownUser() {
    // Only the owner can interact with this site.
    when(buttonInteraction.getUser()).thenReturn(mock(User.class));
    
    listener.onButtonClick(clickEvent);
  
    verify(replyAction, times(1)).complete();
  }
  
  @Test
  public void testIgnoreSelectionByUnknownUser() {
    // Only the owner can interact with this site.
    when(selectionInteraction.getUser()).thenReturn(mock(User.class));
    
    listener.onSelectionMenu(selectionEvent);
  
    verify(replyAction, times(1)).complete();
  }
  
  private class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(new TypeLiteral<Cache<Message, Site>>(){})
            .annotatedWith(Names.named(SITE))
            .toInstance(cache);
    }
  }
}
