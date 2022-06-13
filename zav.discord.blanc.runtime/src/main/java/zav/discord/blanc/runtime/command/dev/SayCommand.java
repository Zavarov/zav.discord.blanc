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

package zav.discord.blanc.runtime.command.dev;

import javax.inject.Inject;
import javax.inject.Named;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.AbstractCommand;

/**
 * This command makes the bot repeat a specified message.
 */
public class SayCommand extends AbstractCommand {
  
  private final SlashCommandEvent event;
  private final String content;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param content The message that should be repeated.
   */
  @Inject
  public SayCommand(SlashCommandEvent event, @Named("content")OptionMapping content) {
    super(Rank.DEVELOPER);
    this.event = event;
    this.content = content.getAsString();
  }
  
  @Override
  public void run() {
    event.reply(content).complete();
  }
}
