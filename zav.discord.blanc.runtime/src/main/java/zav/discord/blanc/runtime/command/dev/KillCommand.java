/*
 * Copyright (c) 2020 Zavarov
 *
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

import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.dv8tion.jda.api.JDA;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.AbstractCommand;

/**
 * This command terminates the whole instance by halting all threads.
 */
@NonNullByDefault
public class KillCommand extends AbstractCommand {

  @Inject
  private Client client;
  
  @Inject
  private ScheduledExecutorService executorService;
  
  public KillCommand() {
    super(Rank.DEVELOPER);
  }
  
  @Override
  public void run() {
    executorService.shutdown();
    for (JDA shard : client.getShards()) {
      shard.shutdown();
    }
  }
}
