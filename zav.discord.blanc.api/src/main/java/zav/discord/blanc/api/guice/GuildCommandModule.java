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
import java.util.Objects;
import javax.inject.Named;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.Contract;

/**
 * A module for injecting all fields of a guild command.
 */
public class GuildCommandModule extends AbstractModule {
  
  private final SlashCommandEvent event;
  
  public GuildCommandModule(SlashCommandEvent event) {
    this.event = event;
  }

  @Override
  @Contract(mutates = "this")
  protected void configure() {
    Objects.requireNonNull(event.getGuild());
    Objects.requireNonNull(event.getMember());

    // General
    bind(JDA.class).toInstance(event.getJDA());
    bind(MessageChannel.class).toInstance(event.getChannel());
    bind(User.class).toInstance(event.getUser());
    bind(SlashCommandEvent.class).toInstance(event);

    // Guild specific
    bind(Guild.class).toInstance(event.getGuild());
    bind(TextChannel.class).toInstance(event.getTextChannel());
    bind(Member.class).toInstance(event.getMember());
    
    // Command Arguments
    for (OptionMapping arg : event.getOptions()) {
      bind(OptionMapping.class).annotatedWith(Names.named(arg.getName())).toInstance(arg);
    }
  }
}
