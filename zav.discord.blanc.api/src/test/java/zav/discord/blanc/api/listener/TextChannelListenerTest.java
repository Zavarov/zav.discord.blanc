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

package zav.discord.blanc.api.listener;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebhookEntity;

/**
 * Checks whether the text channel database is updated whenever the bot leaves a guild or a text
 * channel is deleted.
 */
@ExtendWith(MockitoExtension.class)
public class TextChannelListenerTest {
  
  EntityManagerFactory factory;
  EntityManager entityManager;
  TextChannelListener listener;
  WebhookEntity webhookEntity;
  TextChannelEntity textChannelEntity;
  GuildEntity guildEntity;
  
  @Mock TextChannel textChannel;
  @Mock Guild guild;
  @Mock GuildLeaveEvent leaveEvent;
  @Mock TextChannelDeleteEvent deleteEvent;
  
  static {
    System.setProperty("org.jboss.logging.provider", "slf4j");
  }
  
  /**
   * Initializes the text channel listener.<br>
   * The database used by the listener is initialized with the entities {@code Webhook.json},
   * {@code TextChannel.json} and {@code Guild.json}.
   */
  @BeforeEach
  public void setUp() {
    factory = Persistence.createEntityManagerFactory("discord-entities");
    entityManager = factory.createEntityManager();
    listener = new TextChannelListener(factory);
    
    webhookEntity = new WebhookEntity();
    textChannelEntity = new TextChannelEntity();
    guildEntity = new GuildEntity();
    
    // Bidirectional mapping
    guildEntity.add(webhookEntity);
    guildEntity.add(textChannelEntity);
    textChannelEntity.add(webhookEntity);
    
    entityManager.getTransaction().begin();
    entityManager.merge(guildEntity);
    entityManager.merge(textChannelEntity);
    entityManager.merge(webhookEntity);
    entityManager.getTransaction().commit();
    entityManager.clear();
  }
  
  @AfterEach
  public void tearDown() {
    entityManager.close();
  }
  
  /**
   * Use Case: When leaving a guild, all entries should be deleted from the database.
   */
  @Test
  public void testOnGuildLeave() {    
    when(leaveEvent.getGuild()).thenReturn(guild);
    when(guild.getIdLong()).thenReturn(guildEntity.getId());
    listener.onGuildLeave(leaveEvent);
  
    assertNull(entityManager.find(GuildEntity.class, guildEntity.getId()));
    assertNull(entityManager.find(TextChannelEntity.class, textChannelEntity.getId()));
    assertNull(entityManager.find(WebhookEntity.class, webhookEntity.getId()));
  }
  
  /**
   * Use Case: Do nothing when leaving a guild which isn't persisted.
   */
  @Test
  public void testDoNothingOnGuildLeave() {    
    when(leaveEvent.getGuild()).thenReturn(guild);
    when(guild.getIdLong()).thenReturn(Long.MAX_VALUE);
    listener.onGuildLeave(leaveEvent);
  
    assertNotNull(entityManager.find(GuildEntity.class, guildEntity.getId()));
    assertNotNull(entityManager.find(TextChannelEntity.class, textChannelEntity.getId()));
    assertNotNull(entityManager.find(WebhookEntity.class, webhookEntity.getId()));
  }
  
  /**
   * Use Case: When a text channel is deleted, all corresponding entries should be deleted from the
   * database.
   */
  @Test
  public void testOnTextChannelDelete() {
    when(deleteEvent.getChannel()).thenReturn(textChannel);
    when(textChannel.getIdLong()).thenReturn(textChannelEntity.getId());
    listener.onTextChannelDelete(deleteEvent);
  
    assertNotNull(entityManager.find(GuildEntity.class, guildEntity.getId()));
    assertNull(entityManager.find(TextChannelEntity.class, textChannelEntity.getId()));
    assertNull(entityManager.find(WebhookEntity.class, webhookEntity.getId()));
  }
  
  /**
   * Use Case: Do nothing when a text channel is deleted which hasn't been persisted.
   */
  @Test
  public void testDoNothingOnTextChannelDelete() {
    when(deleteEvent.getChannel()).thenReturn(textChannel);
    when(textChannel.getIdLong()).thenReturn(Long.MAX_VALUE);
    listener.onTextChannelDelete(deleteEvent);
  
    assertNotNull(entityManager.find(GuildEntity.class, guildEntity.getId()));
    assertNotNull(entityManager.find(TextChannelEntity.class, textChannelEntity.getId()));
    assertNotNull(entityManager.find(WebhookEntity.class, webhookEntity.getId()));
  }
}
