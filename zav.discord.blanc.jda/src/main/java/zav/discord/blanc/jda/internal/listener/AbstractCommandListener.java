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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.command.Command;

import java.util.Arrays;
import java.util.List;

/**
 * Abstract base class for both the guild and private command listener.
 */
public abstract class AbstractCommandListener extends ListenerAdapter {
  private static final Logger LOGGER = LogManager.getLogger(AbstractCommandListener.class);
  private final Shard shard;

  protected AbstractCommandListener(Shard shard) {
    this.shard = shard;
  }

  protected void submit(MessageChannel channel, Command command, List<? extends Argument> args) {
    shard.submit(() -> {
      try {
        command.postConstruct(args);
        command.validate();
        command.run();
      } catch (Exception e) {
        LOGGER.warn(e.getMessage(), e);

        EmbedBuilder errorBuilder = new EmbedBuilder();
        
        errorBuilder.setTitle(e.getClass().getSimpleName());
        
        if (StringUtils.isNotBlank(e.getMessage())) {
          errorBuilder.addField("Message", e.getMessage(), false);
        }
      
        if (e.getCause() != null) {
          errorBuilder.addField("Cause", e.getCause().toString(), false);
        }
        
        errorBuilder.addField("StackTrace", StringUtils.join(e.getStackTrace(), '\n'), false);

        channel.sendMessageEmbeds(errorBuilder.build()).complete();
      }
    });
  }
}
