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
import static org.apache.commons.lang3.StringUtils.LF;
import static zav.discord.blanc.api.Constants.SITE;
import static zav.discord.blanc.runtime.internal.DatabaseUtils.getOrCreate;

import com.google.common.cache.Cache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import javax.inject.Named;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebHookEntity;
import zav.discord.blanc.db.GuildTable;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.db.WebHookTable;

/**
 * Displays the guild-specific configuration like subreddit feeds.
 */
public class ConfigurationCommand extends AbstractGuildCommand {
  @Inject
  @Named(SITE)
  private Cache<Message, Site> cache;
  
  @Inject
  private GuildTable guildDb;
  
  @Inject
  private WebHookTable hookDb;
  
  @Inject
  private TextChannelTable textDb;
  
  private GuildEntity guildData;
  
  public ConfigurationCommand() {
    super(MESSAGE_MANAGE);
  }
  
  @Override
  public void postConstruct() {
    guildData = getOrCreate(guildDb, guild);
  }
  
  @Override
  public void run() throws SQLException {
    // Build pages
    List<Site.Page> pages = new ArrayList<>();
    pages.add(createMainPage());
    createBlacklistPage().ifPresent(pages::add);
    createSubredditPage().ifPresent(pages::add);
    
    // Build site
    Site site = Site.create(pages, author.getUser());
    
    // Construct & Send message
    Message message = createMessage(site);

    channel.sendMessage(message).queue(success -> cache.put(success, site));
  }
  
  // ---------------------------------------------------------------------------------------------//
  //                                                                                              //
  //  Message Construction                                                                        //
  //                                                                                              //
  // ---------------------------------------------------------------------------------------------//
  
  private Message createMessage(Site site) {
    SelectionMenu menu = SelectionMenu.create("selectionMenu")
          .addOption("Main Page", "mainPage")
          .addOption("Blacklist", "blacklist")
          .addOption("Subreddits", "subreddits")
          .build();
    
    return new MessageBuilder()
          .setActionRows(ActionRow.of(menu))
          .setEmbeds(site.getCurrentPage())
          .build();
  }
  
  // ---------------------------------------------------------------------------------------------//
  //                                                                                              //
  //  Site Construction                                                                           //
  //                                                                                              //
  // ---------------------------------------------------------------------------------------------//
  
  /**
   * <pre>
   *   +-----------------------+
   *   | Main Page             |
   *   +-----------------------+
   *   |                       |
   *   | Guild Prefix          |
   *   |   ....                |
   *   | Active User           |
   *   |   @...                |
   *   |                       |
   *   +-----------------------+
   * </pre>
   *
   * @return The main page.
   */
  private Site.Page createMainPage() {
    MessageEmbed content = new EmbedBuilder()
          .setTitle(guildData.getName())
          .addField("Guild Prefix", guildData.getPrefix().orElse("<None>"), false)
          .addField("Active User", author.getAsMention(), false)
          .build();
  
    return Site.Page.create("Main Page", List.of(content));
  }
  
  /**
   * <pre>
   *   +-----------------------+
   *   | Forbidden Expressions |
   *   +-----------------------+
   *   |                       |
   *   |   ....                |
   *   |   ....                |
   *   |   ....                |
   *   |                       |
   *   +-----------------------+
   * </pre>
   *
   * @return A site over all banned expressions; Empty if no such expressions exists.
   */
  private Optional<Site.Page> createBlacklistPage() {
    if (guildData.getBlacklist().isEmpty()) {
      return Optional.empty();
    }
    
    String value = guildData.getBlacklist()
          .stream()
          .reduce((u, v) -> u + LF + v)
          .orElse("");
  
    MessageEmbed content = new EmbedBuilder()
          .setTitle("Forbidden Expressions")
          .setDescription(value)
          .build();
    
    Site.Page mainPage = Site.Page.create("Forbidden Expressions", List.of(content));
    return Optional.of(mainPage);
  }
  
  /**
   * <pre>
   *   +-----------------------+
   *   | Subreddit Feeds       |
   *   +-----------------------+
   *   |                       |
   *   | r/....                |
   *   |     #....             |
   *   |     #....             |
   *   | r/....                |
   *   |     #....             |
   *   |                       |
   *   +-----------------------+
   * </pre>
   *
   * @return A site over all registered subreddit feeds; Empty if no subreddits are registered.
   * @throws SQLException If a database error occurred.
   */
  private Optional<Site.Page> createSubredditPage() throws SQLException {
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
