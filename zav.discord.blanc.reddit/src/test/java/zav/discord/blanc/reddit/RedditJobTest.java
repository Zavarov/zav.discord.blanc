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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.db.WebhookTable;

/**
 * Checks whether the Reddit job can recover from the persistent database values.
 */
@ExtendWith(MockitoExtension.class)
public class RedditJobTest {
  
  protected @Mock Client client;
  protected @Mock SubredditObservable observable;
  protected @Mock TextChannelTable textDb;
  protected @Mock WebhookTable hookDb;
  
  protected Injector injector;
  protected RedditJob job;
  
  /**
   * Creates a database for text channels and webhooks and fills it with dummy values.
   *
   * @throws SQLException In case a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    injector = Guice.createInjector(new TestModule());
    job = injector.getInstance(RedditJob.class);
  }
  
  @Test
  public void testRun() {
    RedditJob job = injector.getInstance(RedditJob.class);
    
    job.run();
  
    // Catch all exceptions to prevent the job from ending prematurely
    doThrow(new RuntimeException()).when(observable).notifyAllObservers();
    job.run();
  
    // We can't recover from errors -> terminate
    doThrow(new Error()).when(observable).notifyAllObservers();
    assertThrows(Error.class, job::run);
  }
  
  private class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(Client.class).toProvider(() -> client);
      bind(SubredditObservable.class).toInstance(observable);
      bind(TextChannelTable.class).toInstance(textDb);
      bind(WebhookTable.class).toInstance(hookDb);
    }
  }
}
