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

package zav.discord.blanc.runtime.command.mod;

import static net.dv8tion.jda.api.Permission.MESSAGE_MANAGE;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static zav.discord.blanc.api.Constants.PATTERN;
import static zav.discord.blanc.runtime.internal.DatabaseUtils.getOrCreate;

import com.google.common.cache.Cache;
import java.sql.SQLException;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.inject.Named;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.db.GuildTable;

/**
 * This command blacklists certain words. Any message that contains the word will be deleted by the
 * application.
 */
public class BlacklistCommand extends AbstractGuildCommand {
  @Inject
  private GuildTable db;
  
  @Inject
  @Named(PATTERN)
  private Cache<Long, Pattern> cache;
  
  private GuildEntity guildEntity;
  private String regex;
  
  public BlacklistCommand() {
    super(MESSAGE_MANAGE);
  }
  
  @Override
  public void postConstruct() {
    regex = Objects.requireNonNull(event.getOption("regex")).getAsString();
    guildEntity = getOrCreate(db, guild);
  }
  
  @Override
  public void run() throws SQLException {
    if (guildEntity.getBlacklist().remove(regex)) {
      event.replyFormat(i18n.getString("remove_blacklist"), regex).complete();
    } else {
      //Check if the regex is valid
      Pattern.compile(regex);
  
      guildEntity.getBlacklist().add(regex);
      event.replyFormat(i18n.getString("add_blacklist"), regex).complete();
    }
  
    // Update database
    db.put(guildEntity);
  
    // Update pattern
    String regex = guildEntity.getBlacklist().stream().reduce((u, v) -> u + "|" + v).orElse(EMPTY);
    cache.put(guild.getIdLong(), Pattern.compile(regex));
  }
}
