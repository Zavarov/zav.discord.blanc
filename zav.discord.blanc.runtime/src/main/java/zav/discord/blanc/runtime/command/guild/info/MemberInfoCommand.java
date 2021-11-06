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

package zav.discord.blanc.runtime.command.guild.info;

import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.Argument;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.databind.User;

import java.util.List;

/**
 * This command shows information about the mentioned member.
 */
public class MemberInfoCommand extends AbstractGuildCommand {
  @Nullable
  private User myMember;

  @Override
  public void postConstruct(List<? extends Argument> args) {
    myMember = args.isEmpty() ? author.getAbout() : guild.getMember(args.get(0)).getAbout();
  }

  @Override
  public void run() {
    channel.send(myMember == null ? author.getAbout() : myMember);
  }
}