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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.concurrent.ScheduledExecutorService;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.command.CommandManager;
import zav.discord.blanc.databind.Rank;

/**
 * This command terminates the whole instance by halting all threads.
 */
public class KillCommand extends AbstractCommand {

  private final Shard shard;
  private final Client client;
  private final SlashCommandEvent event;
  private final ScheduledExecutorService eventQueue;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public KillCommand(SlashCommandEvent event, CommandManager manager) {
    super(manager);
    this.event = event;
    this.shard = manager.getShard();
    this.client = shard.getClient();
    this.eventQueue = shard.get(ScheduledExecutorService.class);
  }
  
  @Override
  public Rank getRequiredRank() {
    return Rank.DEVELOPER;
  }

  @Override
  @SuppressFBWarnings(value = "DM_EXIT")
  public void run() {
    event.reply("Goodbye~").setEphemeral(true).complete();

    eventQueue.shutdown();
    
    for (Shard shard : client.getShards()) {
      shard.getJda().shutdown();
    }
    
    System.exit(0);
  }
}
