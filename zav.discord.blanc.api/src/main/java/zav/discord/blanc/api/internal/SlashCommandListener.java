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

import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.Commands;
import zav.discord.blanc.api.guice.GuildCommandModule;
import zav.discord.blanc.api.guice.PrivateCommandModule;

/**
 * This class is responsible for handling and executing all user-commands.
 */
public class SlashCommandListener extends ListenerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(SlashCommandListener.class);
  
  private @Nullable Injector shardInjector;
  private @Nullable ScheduledExecutorService commandQueue;
  
  @Inject
  @Contract(mutates = "this")
  public void setShardInjector(Injector shardInjector) {
    this.shardInjector = shardInjector;
  }
  
  @Inject
  @Contract(mutates = "this")
  public void setCommandQueue(ScheduledExecutorService commandQueue) {
    this.commandQueue = commandQueue;
  }
  
  @Override
  @Contract(mutates = "this")
  public void onSlashCommand(SlashCommandEvent event) {
    Objects.requireNonNull(shardInjector);
    
    // Only respond to a command made by a real person
    if (event.getUser().isBot()) {
      LOGGER.warn("Command {} was triggered by a bot. Ignore...", event.getName());
      return;
    }
    
    Class<? extends Command> clazz = Commands.get(getQualifiedName(event)).orElse(null);
    
    if (clazz == null) {
      LOGGER.error("Unknown slash command {}.", event.getName());
      return;
    }
  
    Module module;
    
    if (event.isFromGuild()) {
      module = new GuildCommandModule(event);
    } else {
      module = new PrivateCommandModule(event);
    }

    Command cmd = shardInjector.createChildInjector(module).getInstance(clazz);
    
    submit(event, cmd);
  }
  
  @Contract(mutates = "this")
  private String getQualifiedName(SlashCommandEvent event) {
    List<String> parts = new ArrayList<>(3);
    
    parts.add(event.getName());
  
    Optional.ofNullable(event.getSubcommandGroup()).ifPresent(parts::add);
    Optional.ofNullable(event.getSubcommandName()).ifPresent(parts::add);
    return String.join(".", parts);
  }
  
  /**
   * Schedules the command to be executed by the {@link #commandQueue} asynchronously.
   *
   * @param event The slash event to which errors are reported to.
   * @param command The command to be executed.
   */
  @Contract(mutates = "this, param2")
  public void submit(SlashCommandEvent event, Command command) {
    Objects.requireNonNull(commandQueue);
    
    commandQueue.submit(() -> {
      try {
        LOGGER.info("Execute {}.", command.getClass());
        command.postConstruct();
        command.validate();
        command.run();
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
