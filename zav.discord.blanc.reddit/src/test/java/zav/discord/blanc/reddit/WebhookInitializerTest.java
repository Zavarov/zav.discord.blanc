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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.requests.RestAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.db.WebhookTable;

/**
 * Checks whether listeners are created for all valid registered webhooks in the database.
 */
@ExtendWith(MockitoExtension.class)
public class WebhookInitializerTest {
  
  @Mock WebhookTable db;
  @Mock SubredditObservable observable;
  @Mock TextChannel textChannel;
  @Mock Webhook webhook;
  @Mock WebhookEntity entity;
  @Mock RestAction<List<Webhook>> action;
  WebhookInitializer initializer;
  
  @BeforeEach
  public void setUp() {
    initializer = new WebhookInitializer(db, observable);
  }
  
  @Test
  public void testLoad() throws SQLException {
    when(textChannel.retrieveWebhooks()).thenReturn(action);
    when(action.complete()).thenReturn(List.of(webhook));
    when(db.get(webhook)).thenReturn(Optional.of(entity));
    when(entity.getSubreddits()).thenReturn(List.of("RedditDev"));
    
    initializer.load(textChannel);
    
    verify(observable).addListener("RedditDev", webhook);
  }
}
