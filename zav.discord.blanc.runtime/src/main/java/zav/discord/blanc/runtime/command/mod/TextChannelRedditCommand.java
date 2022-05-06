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

import static zav.discord.blanc.runtime.internal.DatabaseUtils.getOrCreate;

import java.sql.SQLException;
import javax.inject.Inject;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.db.TextChannelTable;

@Deprecated
public class TextChannelRedditCommand extends AbstractRedditCommand {
  @Inject
  private TextChannelTable db;
  
  private TextChannelEntity entity;
  
  @Override
  public void postConstruct() {
    super.postConstruct();
    entity = getOrCreate(db, target);
  }
  
  @Override
  public void run() throws SQLException {
    // Remove subreddit from database
    if (entity.getSubreddits().contains(subreddit)) {
      entity.getSubreddits().remove(subreddit);
      
      // Update the Reddit job
      observable.removeListener(subreddit, target);
      
      //Update the persistence file
      db.put(entity);
      
      event.replyFormat(i18n.getString("remove_subreddit"), subreddit, target.getAsMention()).complete();
    } else {
      event.reply(i18n.getString("add_subreddit_deprecated")).complete();
    }
  }
}
