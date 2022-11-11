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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.requests.RestAction;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.WebhookEntity;

/**
 * Checks whether listeners are created for all valid registered webhooks in the database.
 */
@ExtendWith(MockitoExtension.class)
public class WebhookInitializerTest {
  
  @Mock SubredditObservable observable;
  @Mock TextChannel textChannel;
  @Mock Guild guild;
  @Mock Member self;
  @Mock Webhook webhook1;
  @Mock Webhook webhook2;
  @Mock RestAction<List<Webhook>> action;
  MockedStatic<WebhookEntity> mocked;
  WebhookInitializer initializer;
  WebhookEntity entity1;
  WebhookEntity entity2;
  
  /**
   * Creates a new instance of the webhook initializer and loads the database with a single entity.
   * The entity is registered to the subreddit {@code RedditDev}.
   */
  @BeforeEach
  public void setUp() {
    when(textChannel.getGuild()).thenReturn(guild);
    when(guild.getSelfMember()).thenReturn(self);
    
    initializer = new WebhookInitializer(observable);
    entity1 = new WebhookEntity();
    entity1.setSubreddits(List.of("RedditDev"));
    entity2 = new WebhookEntity();

    mocked = mockStatic(WebhookEntity.class);
    mocked.when(() -> WebhookEntity.find(webhook1)).thenReturn(entity1);
    mocked.when(() -> WebhookEntity.find(webhook2)).thenReturn(entity2);
  }
  
  @AfterEach
  public void tearDown() {
    mocked.close();
  }
  
  @Test
  public void testLoadWithInsufficientPermission() {
    when(self.hasPermission(any(TextChannel.class), any(Permission.class))).thenReturn(false);
    
    initializer.load(textChannel);

    verify(observable, times(0)).addListener("RedditDev", webhook1);
    verify(observable, times(0)).addListener("RedditDev", webhook2);
  }
  
  @Test
  public void testLoad() {
    when(self.hasPermission(any(TextChannel.class), any(Permission.class))).thenReturn(true);
    when(textChannel.retrieveWebhooks()).thenReturn(action);
    when(action.complete()).thenReturn(List.of(webhook1));
    when(webhook1.getToken()).thenReturn(StringUtils.EMPTY);
    
    initializer.load(textChannel);
    
    verify(observable).addListener("RedditDev", webhook1);
    verify(observable, times(0)).addListener("RedditDev", webhook2);
  }
  
  @Test
  public void testLoadUnrelatedWebhooks() {
    when(self.hasPermission(any(TextChannel.class), any(Permission.class))).thenReturn(true);
    when(textChannel.retrieveWebhooks()).thenReturn(action);
    when(action.complete()).thenReturn(List.of(webhook2));
    
    initializer.load(textChannel);

    verify(observable, times(0)).addListener("RedditDev", webhook1);
    verify(observable, times(0)).addListener("RedditDev", webhook2);
  }
}
