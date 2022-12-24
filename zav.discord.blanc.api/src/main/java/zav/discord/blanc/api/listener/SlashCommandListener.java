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

package zav.discord.blanc.api.listener;

import java.util.concurrent.ScheduledExecutorService;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.CommandParser;
import zav.discord.blanc.api.util.ValidationException;

/**
 * This class is responsible for handling and executing all user-commands.
 */
@NonNullByDefault
public class SlashCommandListener extends ListenerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(SlashCommandListener.class);
  
  private final ScheduledExecutorService commandQueue;
  private final CommandParser commandParser;
  
  /**
   * Creates a new instance of this class.
   *
   * @param commandQueue The shared executor pool.
   * @param commandParser The parser used to create command instances.
   */
  public SlashCommandListener(ScheduledExecutorService commandQueue, CommandParser commandParser) {
    this.commandQueue = commandQueue;
    this.commandParser = commandParser;
  }
  
  @Override
  @Contract(mutates = "this")
  public void onSlashCommand(SlashCommandEvent event) {
    // Only respond to a command made by a real person
    if (event.getUser().isBot()) {
      LOGGER.warn("Command {} was triggered by a bot. Ignore...", event.getName());
      return;
    }
    
    commandParser.parse(event).ifPresent(command -> {
      submit(event, command);
    });
  }
  
  /**
   * Schedules the command to be executed by the {@link #commandQueue} asynchronously.
   *
   * @param event The slash event to which errors are reported to.
   * @param command The command to be executed.
   */
  @Contract(mutates = "this, param2")
  public void submit(SlashCommandEvent event, Command command) {
    commandQueue.submit(() -> {
      try {
        LOGGER.info("Execute {}.", command.getClass());
        command.validate();
        command.run();
      } catch (ValidationException e) {
        LOGGER.debug(e.getMessage(), e);
        event.replyEmbeds(e.getErrorMessage()).queue();
      } catch (Exception e) {
        LOGGER.warn(e.getMessage(), e);
        event.replyEmbeds(buildErrorMessage(e)).queue();
      }
    });
  }
  
  @Contract(pure = true)
  private MessageEmbed buildErrorMessage(Exception e) {
    EmbedBuilder errorBuilder = new EmbedBuilder();
    
    errorBuilder.setTitle(e.getClass().getSimpleName());
    
    if (StringUtils.isNotBlank(e.getMessage())) {
      errorBuilder.addField("Message", e.getMessage(), false);
    }
    
    if (e.getCause() != null) {
      errorBuilder.addField("Cause", e.getCause().toString(), false);
    }
    
    String stackTrace;
    stackTrace = StringUtils.join(e.getStackTrace(), StringUtils.LF);
    stackTrace = StringUtils.truncate(stackTrace, MessageEmbed.VALUE_MAX_LENGTH);
    errorBuilder.addField("StackTrace", stackTrace, false);
    
    return errorBuilder.build();
  }
}
