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
package zav.discord.blanc.runtime.command.guild.mod;

import org.apache.commons.lang3.Validate;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Permission;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.databind.TextChannelValueObject;
import zav.discord.blanc.databind.WebHookValueObject;
import zav.discord.blanc.db.WebHookDatabase;
import zav.discord.blanc.reddit.SubredditObservable;
import zav.discord.blanc.api.TextChannel;
import zav.discord.blanc.api.WebHook;

import java.sql.SQLException;
import java.util.List;

/**
 * This command allows to link subreddits to channels.
 */
public class RedditCommand extends AbstractGuildCommand {
  private static final String WEBHOOK = "Reddit";

  private String mySubreddit;
  private TextChannel myChannel;
  private TextChannelValueObject myChannelData;
  private WebHook myWebHook;
  private WebHookValueObject myWebHookData;
  private GuildValueObject myGuildData;
  
  protected RedditCommand() {
    super(Permission.MANAGE_CHANNELS);
  }
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    Validate.validIndex(args, 0);
    mySubreddit = args.get(0).asString().orElseThrow();
    myChannel = args.size() < 2 ? channel : guild.getTextChannel(args.get(1));
    myChannelData = myChannel.getAbout();
    myWebHook = myChannel.getWebHook(WEBHOOK, true);
    myWebHookData = myWebHook.getAbout();
    myGuildData = guild.getAbout();
  }
  
  @Override
  public void run() throws SQLException {
    // Update view
    if (myWebHookData.getSubreddits().contains(mySubreddit)) {
      // Remove subreddit from database
      myWebHookData.getSubreddits().remove(mySubreddit);
      SubredditObservable.removeListener(mySubreddit, myWebHook);
  
      channel.send("Submissions from r/%s will no longer be posted in %s.", mySubreddit, myChannelData.getName());
  
      // Delete webhook if it's no longer needed
      if (myWebHookData.getSubreddits().isEmpty()) {
        myWebHook.delete();
      }
    } else {
      // Add subreddit to database
      myWebHookData.getSubreddits().add(mySubreddit);
      SubredditObservable.addListener(mySubreddit, myWebHook);
  
      channel.send("Submissions from r/%s will be posted in %s.", mySubreddit, myChannelData.getName());
    }
  
    //Update the persistence file
    WebHookDatabase.put(myGuildData, myChannelData, myWebHookData);
  }
}
