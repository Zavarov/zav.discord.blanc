/*
 * Copyright (c) 2020 Zavarov
 *
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

import net.dv8tion.jda.api.entities.Guild;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.AbstractGuildCommand;

/**
 * This command allows to modify the nickname of the bot in a specified guild.
 */
public class NicknameCommand extends AbstractGuildCommand {
  @Argument(index = 0, useDefault = true)
  @SuppressWarnings({"UnusedDeclaration"})
  private String nickname;
  
  @Argument(index = 1, useDefault = true)
  @SuppressWarnings({"UnusedDeclaration"})
  private Guild defaultGuild;
  
  public NicknameCommand() {
    super(Rank.DEVELOPER);
  }
  
  /**
   * If the new nickname is empty, the old one will be removed. Otherwise it is overwritten.
   */
  @Override
  public void run() {
    defaultGuild.getSelfMember().modifyNickname(nickname).complete();
  }
}
