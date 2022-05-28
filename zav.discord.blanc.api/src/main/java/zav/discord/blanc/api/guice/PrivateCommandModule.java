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

package zav.discord.blanc.api.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.jetbrains.annotations.Contract;

/**
 * A module for injecting all fields of a private command.
 */
public class PrivateCommandModule extends AbstractModule {
  
  private final SlashCommandEvent event;
  
  public PrivateCommandModule(SlashCommandEvent event) {
    this.event = event;
  }
  
  @Override
  @Contract(mutates = "this")
  protected void configure() {
    // General
    bind(JDA.class).toInstance(event.getJDA());
    bind(MessageChannel.class).toInstance(event.getChannel());
    bind(User.class).toInstance(event.getUser());
    bind(SlashCommandEvent.class).toInstance(event);
    
    // Private channel specific
    bind(PrivateChannel.class).toInstance(event.getPrivateChannel());
  
    // Command Arguments
    for (OptionMapping arg : event.getOptions()) {
      bind(OptionMapping.class).annotatedWith(Names.named(arg.getName())).toInstance(arg);
    }
  }
}
