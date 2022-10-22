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

package zav.discord.blanc.runtime.command.core;

import java.util.Objects;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.util.JexlParser;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.command.CommandManager;

/**
 * This command can solve simple mathematical expressions.
 */
public class MathCommand extends AbstractCommand {
  private final SlashCommandEvent event;
  private final JexlParser jexl;
  private final String value;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public MathCommand(SlashCommandEvent event, CommandManager manager) {
    super(manager);
    this.jexl = new JexlParser();
    this.value = Objects.requireNonNull(event.getOption("value")).getAsString();
    this.event = event;
  }
  
  @Override
  public void run() {
    event.reply(jexl.evaluate(value).toString()).complete();
  }
}
