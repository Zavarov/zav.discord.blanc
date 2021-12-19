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

import org.apache.commons.lang3.StringUtils;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.command.Rank;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.api.Guild;

import java.util.List;

/**
 * This command allows to modify the nickname of the bot in a specified guild.
 */
public class NicknameCommand extends AbstractGuildCommand {
  private String myNickname;
  private Guild myGuild;
  
  public NicknameCommand() {
    super(Rank.DEVELOPER);
  }
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    myNickname = args.size() > 0 ? args.get(0).asString().orElseThrow() : StringUtils.EMPTY;
    myGuild = args.size() > 1 ? shard.getGuild(args.get(1)) : guild;
  }
  
  /**
   * If the new nickname is empty, the old one will be removed. Otherwise it is overwritten.
   */
  @Override
  public void run() {
    myGuild.getSelfMember().setNickname(myNickname);
  }
}
