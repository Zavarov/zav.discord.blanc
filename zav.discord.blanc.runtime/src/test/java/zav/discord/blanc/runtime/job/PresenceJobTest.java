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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * This test case checks whether the status message for all shards are properly updated.
 */
@ExtendWith(MockitoExtension.class)
public class PresenceJobTest extends AbstractTest {
  PresenceJob job;
  
  /**
   * Initializes the presence job with a mocked Discord client over two shards.
   *
   * @throws IOException Should never be thrown.
   */
  @BeforeEach
  public void setUp() throws IOException {
    job = new PresenceJob(client);
  }
  
  @Test
  public void testRun() { 
    job.run();
    
    verify(presence).setActivity(any());
  }
}
