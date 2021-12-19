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
import zav.discord.blanc.db.WebHookTable;
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
  
  protected RedditCommand() {
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
    WebHook myWebhook = myChannel.getWebHook(WEBHOOK, true);
  
    // Update view
    if (myWebhook.getAbout().getSubreddits().contains(mySubreddit)) {
      // Remove subreddit from database
      myWebhook.getAbout().getSubreddits().remove(mySubreddit);
      SubredditObservable.removeListener(mySubreddit, myWebhook);
  
      channel.send("Submissions from r/%s will no longer be posted in %s.", mySubreddit, myChannel.getAbout().getName());
  
      // Delete webhook if it's no longer needed
      if (myWebhook.getAbout().getSubreddits().isEmpty()) {
        myWebhook.delete();
      }
    } else {
      // Add subreddit to database
      myWebhook.getAbout().getSubreddits().add(mySubreddit);
      SubredditObservable.addListener(mySubreddit, myWebhook);
  
      channel.send("Submissions from r/%s will be posted in %s.", mySubreddit, myChannel.getAbout().getName());
    }
  
    //Update the persistence file
    WebHookTable.put(guild.getAbout(), myChannel.getAbout(), myWebhook.getAbout());
  }
}
