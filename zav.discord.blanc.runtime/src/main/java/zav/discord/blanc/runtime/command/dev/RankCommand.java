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

import static zav.discord.blanc.runtime.internal.DatabaseUtils.getOrCreate;

import java.sql.SQLException;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.User;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.UserTable;

/**
 * This command assigns and removes the Reddit rank.
 */
public class RankCommand extends AbstractCommand {
  @Argument(index = 0)
  @SuppressWarnings({"UnusedDeclaration"})
  private Rank rank;
  
  @Argument(index = 1, useDefault = true)
  @SuppressWarnings({"UnusedDeclaration"})
  private User user;
  
  @Inject
  private UserTable userTable;
  
  private UserEntity userEntity;
  private String rankName;
  private String userName;
  
  public RankCommand() {
    super(Rank.DEVELOPER);
  }
  
  @Override
  public void postConstruct() {
    userEntity = getOrCreate(userTable, user);
    rankName = rank.name();
    userName = userEntity.getName();
  }
  
  @Override
  public void run() throws SQLException {
    // Can the author grant the role?
    if (Rank.getEffectiveRanks(userTable, author).contains(rank)) {
      // Does the user have the rank? => add
      if (userEntity.getRanks().contains(rankName)) {
        userEntity.getRanks().remove(rankName);
        channel.sendMessageFormat(i18n.getString("remove_rank"), rankName, userName).complete();
      } else {
        userEntity.getRanks().add(rankName);
        channel.sendMessageFormat(i18n.getString("add_rank"), rankName, userName).complete();
      }
      userTable.put(userEntity);
    } else {
      channel.sendMessageFormat(i18n.getString("insufficient_rank"), rankName).complete();
    }
  }
}