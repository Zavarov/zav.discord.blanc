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

package zav.discord.blanc.runtime.command.core;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.command.CommandManager;
import zav.discord.blanc.databind.Credentials;

/**
 * This command prints an invitation link to the support server.
 */
public class SupportCommand extends AbstractCommand {
  
  private final SlashCommandEvent event;
  private final String link;
  private final Client client;
  private final Shard shard;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public SupportCommand(SlashCommandEvent event, CommandManager manager) {
    super(manager);
    this.event = event;
    this.shard = manager.getShard();
    this.client = shard.getClient();
    this.link = client.get(Credentials.class).getInviteSupportServer();
  }
  
  @Override
  public void run() {
    event.reply(getMessage("server_invitation", link)).complete();
  }
}
