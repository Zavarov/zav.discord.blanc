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

import org.apache.commons.lang3.Validate;
import zav.discord.blanc.Rank;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.Argument;
import zav.discord.blanc.databind.User;
import zav.discord.blanc.db.UserTable;

import java.sql.SQLException;
import java.util.List;

/**
 * This command assigns and removes the Reddit rank.
 */
public class RankCommand extends AbstractCommand {
  private Rank myRank;
  private User myUser;
  
  public RankCommand() {
    super(Rank.DEVELOPER);
  }
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    Validate.validIndex(args, 0);
    myUser = shard.getUser(args.get(0)).getAbout();
    Validate.validIndex(args, 1);
    myRank = args.get(1).asString().map(String::toUpperCase).map(Rank::valueOf).orElseThrow();
  }
  
  @Override
  public void run() throws SQLException {
    // Can the author grant the role?
    if (author.getAbout().getRanks().contains(myRank.name())) {
      // Does the user have the rank? => add
      if (myUser.getRanks().contains(myRank.name())) {
        myUser.getRanks().remove(myRank.name());
        channel.send("Removed rank \"%s\" from %s.", myRank.name(), myUser.getName());
      } else {
        myUser.getRanks().add(myRank.name());
        channel.send("Granted rank \"%s\" to %s.", myRank.name(), myUser.getName());
      }
      UserTable.put(myUser);
    } else {
      channel.send("You lack the rank to grant the \"%s\" Rank", myRank.name());
    }
  }
}