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

import static zav.discord.blanc.api.Rank.USER;
import static zav.discord.blanc.api.Rank.getEffectiveRanks;

import java.util.ResourceBundle;
import javax.inject.Inject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;
import zav.discord.blanc.api.Command;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.db.UserTable;

/**
 * Abstract base class for all commands.<br>
 * Commands can be either executed in a guild or private channel.
 */
@NonNullByDefault
public abstract class AbstractCommand implements Command {
  @Inject
  protected JDA shard;
  @Inject
  protected MessageChannel channel;
  @Inject
  protected User author;
  @Inject
  protected Message message;
  @Inject
  private UserTable db;
  
  protected ResourceBundle i18n;
  
  private final Rank requiredRank;
  
  protected AbstractCommand(Rank requiredRank) {
    this.requiredRank = requiredRank;
    this.i18n = ResourceBundle.getBundle("i18n");
  }
  
  protected AbstractCommand() {
    this(USER);
  }
  
  @Override
  @Contract(pure = true)
  public void validate() throws Exception {
    // Does the user have the required rank?
    if (!getEffectiveRanks(db, author).contains(requiredRank)) {
      throw new InsufficientRankException(requiredRank);
    }
  }
}
