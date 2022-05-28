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

import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.AbstractCommand;

/**
 * This command terminates the whole instance by halting all threads.
 */
public class KillCommand extends AbstractCommand {

  private final Client client;
  private final SlashCommandEvent event;
  private final ScheduledExecutorService queue;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param queue The job queue.
   * @param client The bot instance over all shards.
   */
  @Inject
  public KillCommand(SlashCommandEvent event, ScheduledExecutorService queue, Client client) {
    super(Rank.DEVELOPER);
    this.event = event;
    this.queue = queue;
    this.client = client;
  }
  
  @Override
  public void run() {
    event.reply("Goodbye~").setEphemeral(true).complete();

    queue.shutdown();
    for (JDA shard : client.getShards()) {
      shard.shutdown();
    }
  }
}
