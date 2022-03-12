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

package zav.discord.blanc.runtime.command.mod.legacy;

import static net.dv8tion.jda.api.Permission.MESSAGE_MANAGE;
import static zav.discord.blanc.runtime.internal.DatabaseUtils.getOrCreate;

import java.sql.SQLException;
import java.util.Locale;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.TextChannel;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.reddit.SubredditObservable;

/**
 * This command links subreddits to Discord channels.
 */
public class RedditCommandLegacy extends AbstractGuildCommand {
  @Argument(index = 0)
  @SuppressWarnings({"UnusedDeclaration"})
  private String subreddit;
  
  @Argument(index = 1, useDefault = true)
  @SuppressWarnings({"UnusedDeclaration"})
  private TextChannel target;
  
  @Inject
  private SubredditObservable observable;
  
  @Inject
  private TextChannelTable db;
  
  private TextChannelEntity entity;
    
  protected RedditCommandLegacy() {
    super(MESSAGE_MANAGE);
  }
  
  @Override
  public void postConstruct() {
    entity = getOrCreate(db, target);
    subreddit = subreddit.toLowerCase(Locale.ENGLISH);
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
 
      channel.sendMessageFormat(i18n.getString("remove_subreddit"), subreddit, target.getAsMention()).complete();
    } else {
      channel.sendMessage(i18n.getString("add_subreddit_deprecated")).complete();
    }
  }
}
