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

package zav.discord.blanc.reddit.internal;

import java.sql.SQLException;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.reddit.SubredditObservable;

/**
 * Utility class for initializing all subreddit feeds that have been mapped to a
 * {@link TextChannel}.
 */
public class TextChannelInitializer {
  private static final Logger LOGGER = LoggerFactory.getLogger(TextChannelInitializer.class);
  
  private final TextChannelTable db;
  
  private final SubredditObservable observable;
  
  @Inject
  public TextChannelInitializer(TextChannelTable db, SubredditObservable observable) {
    this.db = db;
    this.observable = observable;
  }
  
  public void load(Guild guild) throws SQLException {
    for (TextChannel textChannel : guild.getTextChannels()) {
      load(textChannel);
    }
  }
  
  private void load(TextChannel textChannel) throws SQLException {
    db.get(textChannel).ifPresent(entity -> {
      for (String subreddit : entity.getSubreddits()) {
        observable.addListener(subreddit, textChannel);
        LOGGER.info("Add subreddit '{}' to textChannel '{}'.", subreddit, textChannel.getName());
      }
    });
  }
}
