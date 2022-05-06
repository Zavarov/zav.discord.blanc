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

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.LF;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebHookEntity;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.db.WebHookTable;

public class SubredditFeedConfigurationCommand extends AbstractConfigurationCommand {
  
  @Inject
  private WebHookTable hookDb;
  
  @Inject
  private TextChannelTable textDb;
  
  @Override
  protected Optional<Site.Page> createPage() throws SQLException {
    // Subreddit Name -> Text Channels
    Multimap<String, String> subreddits = HashMultimap.create();
    
    // Get all subreddit feeds
    for (WebHookEntity entity : hookDb.get(guildData.getId())) {
      for (String subredditName : entity.getSubreddits()) {
        TextChannel textChannel = guild.getTextChannelById(entity.getChannelId());
        // Channel may have been deleted
        if (textChannel != null) {
          subreddits.put(subredditName, textChannel.getAsMention());
        }
      }
    }
    
    for (TextChannelEntity entity : textDb.get(guildData.getId())) {
      for (String subredditName : entity.getSubreddits()) {
        TextChannel textChannel = guild.getTextChannelById(entity.getId());
        // Channel may have been deleted
        if (textChannel != null) {
          subreddits.put(subredditName, textChannel.getAsMention());
        }
      }
    }
    
    if (subreddits.isEmpty()) {
      return Optional.empty();
    }
    
    EmbedBuilder builder = new EmbedBuilder();
    builder.setTitle("Subreddit Feeds");
    
    // Create a field for each subreddit
    subreddits.asMap().forEach((key, values) -> {
      String value = values.stream().reduce((u, v) -> u + LF + v).orElse(EMPTY);
      builder.addField("r/" + key, value, false);
    });
    
    MessageEmbed content = builder.build();
    
    Site.Page mainPage = Site.Page.create("Subreddit Feeds", List.of(content));
    return Optional.of(mainPage);
  }
}
