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

package zav.discord.blanc.api.internal;

import static net.dv8tion.jda.api.entities.MessageEmbed.VALUE_MAX_LENGTH;
import static org.apache.commons.lang3.StringUtils.LF;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.join;
import static org.apache.commons.lang3.StringUtils.truncate;

import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Command;

/**
 * Abstract base class for both the guild and private command listener.
 */
@NonNullByDefault
public abstract class AbstractCommandListener extends ListenerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCommandListener.class);

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
        channel.sendMessageEmbeds(buildErrorMessage(e)).complete();
      }
    });
  }
  
  private MessageEmbed buildErrorMessage(Exception e) {
    EmbedBuilder errorBuilder = new EmbedBuilder();
  
    errorBuilder.setTitle(e.getClass().getSimpleName());
  
    if (isNotBlank(e.getMessage())) {
      errorBuilder.addField("Message", e.getMessage(), false);
    }
  
    if (e.getCause() != null) {
      errorBuilder.addField("Cause", e.getCause().toString(), false);
    }
  
    String stackTrace = join(e.getStackTrace(), LF);
    errorBuilder.addField("StackTrace", truncate(stackTrace, VALUE_MAX_LENGTH), false);
    
    return errorBuilder.build();
  }
}
