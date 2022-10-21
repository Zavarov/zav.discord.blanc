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

package zav.discord.blanc.runtime.job;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.reddit.TextChannelInitializer;
import zav.discord.blanc.reddit.WebhookInitializer;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * Checks whether the Reddit job can recover from the persistent database values.
 */
@SuppressWarnings("deprecation")
@ExtendWith(MockitoExtension.class)
public class RedditJobTest extends AbstractTest {  
  @Mock TextChannelInitializer textInitializer;
  @Mock WebhookInitializer hookInitializer;
  
  RedditJob job;
  
  /**
   * Creates a database for text channels and webhooks and fills it with dummy values.
   */
  @BeforeEach
  public void setUp() {
    job = new RedditJob(client);
  }
  
  @Test
  public void testPostConstruct() {
    job.postConstruct(client, textInitializer);
    job.postConstruct(client, hookInitializer);
    
    when(channel.canTalk()).thenReturn(false);
    // User can't talk in the channel
    verify(hookInitializer, times(0)).load(any());
    verify(textInitializer).load(any());
    
    when(channel.canTalk()).thenReturn(true);
    job.postConstruct(client, hookInitializer);
    verify(hookInitializer).load(any());
  }
  
  @Test
  public void testRun() {
    job.run();
  
    // Catch all exceptions to prevent the job from ending prematurely
    doThrow(new RuntimeException()).when(subredditObservable).notifyAllObservers();
    job.run();
  
    // We can't recover from errors -> terminate
    doThrow(new Error()).when(subredditObservable).notifyAllObservers();
    assertThrows(Error.class, job::run);
  }
}
