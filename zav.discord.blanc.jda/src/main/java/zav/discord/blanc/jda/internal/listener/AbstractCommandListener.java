/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.jda.internal.listener;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.command.Command;

/**
 * Abstract base class for both the guild and private command listener.
 */
public abstract class AbstractCommandListener extends ListenerAdapter {
  private static final Logger LOGGER = LogManager.getLogger(AbstractCommandListener.class);
  private final Shard shard;

  protected AbstractCommandListener(Shard shard) {
    this.shard = shard;
  }

  protected void submit(MessageChannel channel, Command command) {
    shard.submit(() -> {
      try {
        command.validate();
        command.run();
      } catch (Exception e) {
        LOGGER.warn(e.getMessage(), e);
        channel.sendMessage(e.getMessage()).complete();
      }
    });
  }
}