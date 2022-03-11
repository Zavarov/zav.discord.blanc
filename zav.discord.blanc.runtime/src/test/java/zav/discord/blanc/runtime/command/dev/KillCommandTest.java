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

package zav.discord.blanc.runtime.command.dev;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.runtime.command.AbstractDevCommandTest;

/**
 * Check whether all threads are terminated.
 */
@ExtendWith(MockitoExtension.class)
public class KillCommandTest  extends AbstractDevCommandTest {
  
  @Test
  public void testCommandIsOfCorrectType() {
    check("b:dev.kill", KillCommand.class);
  }
  
  @Test
  public void testShutdown() throws Exception {
    when(client.getShards()).thenReturn(List.of(shard));
    
    run("b:dev.kill");
    
    verify(shard, times(1)).shutdown();
  }
}
