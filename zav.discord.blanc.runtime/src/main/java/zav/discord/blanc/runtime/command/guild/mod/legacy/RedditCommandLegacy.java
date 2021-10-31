/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.runtime.command.guild.mod.legacy;

import org.apache.commons.lang3.Validate;
import zav.discord.blanc.Argument;
import zav.discord.blanc.Permission;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.view.TextChannelView;

import java.sql.SQLException;
import java.util.List;

/**
 * This command allows to link subreddits to channels.
 */
public class RedditCommandLegacy extends AbstractGuildCommand {
    
  private String mySubreddit;
  private TextChannelView myChannel;
    
  protected RedditCommandLegacy() {
    super(Permission.MANAGE_CHANNELS);
  }
    
  @Override
  public void postConstruct(List<? extends Argument> args) {
    Validate.validIndex(args, 0);
    mySubreddit = args.get(0).asString().orElseThrow();
    myChannel = args.size() < 2 ? channel : guild.getTextChannel(args.get(1));
  }
    
  @Override
  public void run() throws SQLException {
    // Remove subreddit from database
    if (myChannel.getAbout().getSubreddits().contains(mySubreddit)) {
      myChannel.getAbout().getSubreddits().remove(mySubreddit);
  
      // Update view
      channel.updateSubreddit(mySubreddit);
  
      //Update the persistence file
      TextChannelTable.put(guild.getAbout(), myChannel.getAbout());
 
      channel.send("Submissions from r/%s will no longer be posted in %s.", mySubreddit, myChannel.getAbout().getName());
    } else {
      channel.send("This functionality is deprecated. Please use the `reddit` command instead.");
    }
  }
}
