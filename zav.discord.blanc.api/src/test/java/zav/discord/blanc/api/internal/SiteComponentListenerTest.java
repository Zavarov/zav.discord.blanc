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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Site;

/**
 * Checks whether site interactions are processed correctly.
 */
@ExtendWith(MockitoExtension.class)
public class SiteComponentListenerTest extends AbstractListenerTest {
  SiteComponentListener listener;
  ButtonClickEvent clickEvent;
  SelectionMenuEvent selectionEvent;
  Site site;
  Site.Page page;
  
  final String left = "left";
  final String right = "right";
  final long responseNumber = 11111L;
  
  @Mock JDA jda;
  @Mock User user;
  @Mock ButtonInteraction buttonInteraction;
  @Mock SelectionMenuInteraction selectionInteraction;
  @Mock Message message;
  @Mock Button button;
  @Mock ReplyAction replyAction;
  @Mock UpdateInteractionAction updateAction;
  @Mock MessageEmbed mainPage;
  
  /**
   * Initialize a site and fictitious Button and SelectionMenu interactions which are used to
   * modify the page.
   */
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    page = Site.Page.create("mainPage", List.of(mainPage));
    site = spy(Site.create(List.of(page), user));
    
    clickEvent = new ButtonClickEvent(jda, responseNumber, buttonInteraction);
    selectionEvent = new SelectionMenuEvent(jda, responseNumber, selectionInteraction);
    siteCache.put(message, site);
  
    listener = injector.getInstance(SiteComponentListener.class);
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
    when(buttonInteraction.getMessage()).thenReturn(message);
    when(buttonInteraction.getUser()).thenReturn(user);
    // Null for ephemeral message
    when(buttonInteraction.getButton()).thenReturn(null);
    
    listener.onButtonClick(clickEvent);
  
    verify(site, times(0)).moveLeft();
    verify(site, times(0)).moveRight();
  }
  
  @Test
  public void testIgnoreClickOnUnknownId() {
    when(buttonInteraction.getMessage()).thenReturn(message);
    when(buttonInteraction.getUser()).thenReturn(user);
    when(buttonInteraction.getButton()).thenReturn(button);
    // Null for unknown button ids
    when(button.getId()).thenReturn(null);
    
    listener.onButtonClick(clickEvent);
  
    verify(site, times(0)).moveLeft();
    verify(site, times(0)).moveRight();
  }
  
  @Test
  public void testClickLeft() {
    when(buttonInteraction.getMessage()).thenReturn(message);
    when(buttonInteraction.getUser()).thenReturn(user);
    when(buttonInteraction.getButton()).thenReturn(button);
    when(button.getId()).thenReturn(left);
    when(buttonInteraction.deferEdit()).thenReturn(updateAction);
    when(updateAction.setEmbeds(any(MessageEmbed.class))).thenReturn(updateAction);
  
    listener.onButtonClick(clickEvent);
  
    verify(site, times(1)).moveLeft();
    verify(site, times(0)).moveRight();
  }
  
  @Test
  public void testClickRight() {
    when(buttonInteraction.getMessage()).thenReturn(message);
    when(buttonInteraction.getUser()).thenReturn(user);
    when(buttonInteraction.getButton()).thenReturn(button);
    when(button.getId()).thenReturn(right);
    when(buttonInteraction.deferEdit()).thenReturn(updateAction);
    when(updateAction.setEmbeds(any(MessageEmbed.class))).thenReturn(updateAction);
  
    listener.onButtonClick(clickEvent);
  
    verify(site, times(0)).moveLeft();
    verify(site, times(1)).moveRight();
  }
  
  @Test
  public void testClickInvalidId() {
    when(buttonInteraction.getMessage()).thenReturn(message);
    when(buttonInteraction.getUser()).thenReturn(user);
    when(buttonInteraction.getButton()).thenReturn(button);
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
    when(selectionInteraction.getMessage()).thenReturn(message);
    when(selectionInteraction.getUser()).thenReturn(user);
    // Interaction with two selected items
    when(selectionInteraction.getValues()).thenReturn(List.of(EMPTY, EMPTY));
  
    listener.onSelectionMenu(selectionEvent);
  
    verify(site, times(0)).moveLeft();
    verify(site, times(0)).moveRight();
  }
  
  @Test
  public void testChangeSelection() {
    when(selectionInteraction.getMessage()).thenReturn(message);
    when(selectionInteraction.getUser()).thenReturn(user);
    when(selectionInteraction.getValues()).thenReturn(List.of("mainPage"));
    when(selectionInteraction.deferEdit()).thenReturn(updateAction);
    when(updateAction.setEmbeds(any(MessageEmbed.class))).thenReturn(updateAction);
    
    listener.onSelectionMenu(selectionEvent);
  
    verify(site, times(1)).changeSelection(any());
  }
  
  @Test
  public void testIgnoreButtonByUnknownUser() {
    when(buttonInteraction.getMessage()).thenReturn(message);
    when(buttonInteraction.deferReply()).thenReturn(replyAction);
    // Only the owner can interact with this site.
    when(buttonInteraction.getUser()).thenReturn(mock(User.class));
    
    listener.onButtonClick(clickEvent);
  
    verify(replyAction, times(1)).complete();
  }
  
  @Test
  public void testIgnoreSelectionByUnknownUser() {
    when(selectionInteraction.getMessage()).thenReturn(message);
    when(selectionInteraction.deferReply()).thenReturn(replyAction);
    // Only the owner can interact with this site.
    when(selectionInteraction.getUser()).thenReturn(mock(User.class));
    
    listener.onSelectionMenu(selectionEvent);
  
    verify(replyAction, times(1)).complete();
  }
}