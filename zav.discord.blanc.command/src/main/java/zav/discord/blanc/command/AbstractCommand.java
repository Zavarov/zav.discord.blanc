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

package zav.discord.blanc.command;

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.databind.Rank;

/**
 * Abstract base class for all commands.<br>
 * Commands can be either executed in a guild or private channel.
 */
@NonNullByDefault
public abstract class AbstractCommand implements Command {
  private final SlashCommandEvent event;
  private final CommandManager manager;
  private final ResourceBundle i18n;
  
  /**
   * Creates a new instance of this class.
   *
   * @param event The event triggering this command.
   * @param manager The command-specific manager.
   */
  protected AbstractCommand(SlashCommandEvent event, CommandManager manager) {
    this.event = event;
    this.manager = manager;
    this.i18n = ResourceBundle.getBundle("i18n");
  }

  protected Rank getRequiredRank() {
    return Rank.USER;
  }
  
  @Override
  @Contract(pure = true)
  public void validate() throws ExecutionException {
    // Does the user have the required rank?
    manager.validate(getRequiredRank());
  }
  
  /**
   * Returns the internationalized message with the given key.
   * The messages are stored in a file with name {@code i18n_XX.properties} in the local class path,
   * with {@code XX} being the country code (e.g en, ru, ...).
   *
   * @param key The id of the i18n message.
   * @param args Additional arguments which may be injected into the message.
   * @return An internationalized string.
   * @see String#format(String, Object...)
   */
  protected String getMessage(String key, Object... args) {
    return String.format(i18n.getString(key), args);
  }

  protected void reply(Object content, boolean ephemeral) {
    event.reply(MarkdownSanitizer.escape(content.toString())).setEphemeral(ephemeral).complete();
  }

  protected void reply(Object content) {
    reply(content, false);
  }

  protected void reply(String pattern, Object... args) {
    reply(MessageFormat.format(pattern, args));
  }

  protected void reply(MessageEmbed messageEmbed) {
    event.replyEmbeds(messageEmbed).complete();
  }
}
