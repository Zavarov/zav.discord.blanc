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

import com.google.inject.Injector;
import com.google.inject.Module;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import javax.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.Commands;
import zav.discord.blanc.api.guice.GuildCommandModule;
import zav.discord.blanc.api.guice.PrivateCommandModule;

public class SlashCommandListener extends ListenerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(SlashCommandListener.class);
  
  private Injector shardInjector;
  private ScheduledExecutorService commandQueue;
  
  /*package*/ SlashCommandListener() {
    // Create instance with Guice
  }
  
  @Inject
  @Contract(mutates = "this")
  /*package*/ void setShardInjector(Injector shardInjector) {
    this.shardInjector = shardInjector;
  }
  
  @Inject
  @Contract(mutates = "this")
  /*package*/ void setCommandQueue(ScheduledExecutorService commandQueue) {
    this.commandQueue = commandQueue;
  }
  
  @Override
  @Contract(mutates = "this")
  public void onSlashCommand(SlashCommandEvent event) {
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
  
    Module module = event.isFromGuild() ? new GuildCommandModule(event) : new PrivateCommandModule(event);

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
  
  @Contract(mutates = "this, param2")
  /*package*/ void submit(SlashCommandEvent event, Command command) {
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
