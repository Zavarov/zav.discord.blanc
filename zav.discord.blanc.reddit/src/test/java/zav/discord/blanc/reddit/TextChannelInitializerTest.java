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

package zav.discord.blanc.reddit;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.TextChannelEntity;

/**
 * Checks whether listeners are created for all valid registered text channels in the database.
 */
@Deprecated
@ExtendWith(MockitoExtension.class)
public class TextChannelInitializerTest {
  
  @Mock SubredditObservable observable;
  @Mock Guild guild;
  @Mock TextChannel textChannel;
  EntityManagerFactory factory;
  EntityManager entityManager;
  TextChannelEntity entity;
  TextChannelInitializer initializer;
  
  static {
    System.setProperty("org.jboss.logging.provider", "slf4j");
  }
  
  /**
   * Creates a new instance of the text channel initializer and loads the database with a single
   * entity. The entity is registered to the subreddit {@code RedditDev}.
   */
  @BeforeEach
  public void setUp() {
    factory = Persistence.createEntityManagerFactory("discord-entities");
    initializer = new TextChannelInitializer(factory, observable);
    entityManager = factory.createEntityManager();
    entity = new TextChannelEntity();
    entity.setSubreddits(List.of("RedditDev"));
    
    entityManager.getTransaction().begin();
    entityManager.merge(entity);
    entityManager.getTransaction().commit();
  }
  
  @AfterEach
  public void tearDown() {
    entityManager.close();
  }
  
  @Test
  public void testLoad() {
    when(guild.getTextChannels()).thenReturn(List.of(textChannel));
    when(textChannel.getIdLong()).thenReturn(entity.getId());
    
    initializer.load(guild);
    
    verify(observable).addListener("RedditDev", textChannel);
  }
  
  @Test
  public void testLoadUnrelatedTextChannels() {
    when(guild.getTextChannels()).thenReturn(List.of(textChannel));
    when(textChannel.getIdLong()).thenReturn(Long.MAX_VALUE);
    
    initializer.load(guild);
    
    verify(observable, times(0)).addListener("RedditDev", textChannel);
  }
}
