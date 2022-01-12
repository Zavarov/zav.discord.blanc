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

package zav.discord.blanc.api.internal.listener;

import static org.apache.commons.lang3.StringUtils.LF;

import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.api.command.Command;

/**
 * Abstract base class for both the guild and private command listener.
 */
public abstract class AbstractCommandListener extends ListenerAdapter {
  private static final Logger LOGGER = LogManager.getLogger(AbstractCommandListener.class);

  @Inject
  private ScheduledExecutorService queue;

  protected void submit(MessageChannel channel, Command command) {
    queue.submit(() -> {
      try {
        command.postConstruct();
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
        
        errorBuilder.addField("StackTrace", StringUtils.join(e.getStackTrace(), LF), false);

        channel.sendMessageEmbeds(errorBuilder.build()).complete();
      }
    });
  }
}
