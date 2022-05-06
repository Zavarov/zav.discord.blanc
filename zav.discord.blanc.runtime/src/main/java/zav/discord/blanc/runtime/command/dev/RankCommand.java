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

import static zav.discord.blanc.runtime.internal.DatabaseUtils.getOrCreate;

import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.UserTable;

/**
 * This command assigns and removes the Reddit rank.
 */
public class RankCommand extends AbstractCommand {
  @Inject
  private UserTable userTable;
  
  private UserEntity userEntity;
  private String rankName;
  private String userName;
  
  private Rank rank;
  private User user;
  
  public RankCommand() {
    super(Rank.DEVELOPER);
  }
  
  @Override
  public void postConstruct() {
    rank = Rank.valueOf(Objects.requireNonNull(event.getOption("rank")).getAsString().toUpperCase(Locale.ENGLISH));
    user = Optional.ofNullable(event.getOption("user")).map(OptionMapping::getAsUser).orElse(author);
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
        event.replyFormat(i18n.getString("remove_rank"), rankName, userName).complete();
      } else {
        userEntity.getRanks().add(rankName);
        event.replyFormat(i18n.getString("add_rank"), rankName, userName).complete();
      }
      userTable.put(userEntity);
    } else {
      event.replyFormat(i18n.getString("insufficient_rank"), rankName).complete();
    }
  }
}