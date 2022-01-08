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

import static zav.discord.blanc.runtime.internal.ArgumentImpl.of;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Permission;
import zav.discord.blanc.api.site.Site;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.databind.GuildDto;
import zav.discord.blanc.databind.RoleDto;
import zav.discord.blanc.databind.TextChannelDto;
import zav.discord.blanc.databind.WebHookDto;
import zav.discord.blanc.databind.message.MessageEmbedDto;
import zav.discord.blanc.databind.message.PageDto;
import zav.discord.blanc.databind.message.SiteDto;
import zav.discord.blanc.db.RoleDatabase;
import zav.discord.blanc.db.TextChannelDatabase;
import zav.discord.blanc.db.WebHookDatabase;

public class ConfigurationCommand extends AbstractGuildCommand {
  private GuildDto guildData;
  
  public ConfigurationCommand() {
    super(Permission.MANAGE_MESSAGES);
  }
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    guildData = guild.getAbout();
  }
  
  @Override
  public void run() throws SQLException {
    List<SiteDto> sites = new ArrayList<>();
    
    sites.add(createMainSite());
    createBlacklistSite().ifPresent(sites::add);
    createSubredditSite().ifPresent(sites::add);
    createSelfAssignableRolesSite().ifPresent(sites::add);
    
    channel.send(Site.of(sites), sites);
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
  private SiteDto createMainSite() {
    MessageEmbedDto content = new MessageEmbedDto();
    content.setTitle(guildData.getName());
    content.addField("Guild Prefix", guildData.getPrefix().orElse("<None>"));
    content.addField("Active User", author.getAsMention());
    
    PageDto mainPage = new PageDto()
          .withContent(content);
    
    return new SiteDto()
          .withPages(List.of(mainPage))
          .withLabel("Main Page")
          .withDescription("Go back to the main page.");
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
  private Optional<SiteDto> createBlacklistSite() {
    if (guildData.getBlacklist().isEmpty()) {
      return Optional.empty();
    }
    
    String value = guildData.getBlacklist()
          .stream()
          .reduce((u, v) -> u + StringUtils.LF + v)
          .orElse("");
  
    MessageEmbedDto content = new MessageEmbedDto();
    content.setTitle("Forbidden Expressions");
    content.setContent(value);
    
    PageDto blacklistPage = new PageDto()
          .withContent(content);
    
    SiteDto site = new SiteDto()
          .withPages(List.of(blacklistPage))
          .withLabel("Forbidden Expressions")
          .withDescription("All words that are banned in this guild.");
  
    return Optional.of(site);
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
  private Optional<SiteDto> createSubredditSite() throws SQLException {
    // Subreddit Name -> Text Channels
    Multimap<String, String> subreddits = HashMultimap.create();
    
    // Get all subreddit feeds
    for (WebHookDto webHook : WebHookDatabase.getAll(guildData.getId())) {
      for (String subredditName : webHook.getSubreddits()) {
        subreddits.put(subredditName, guild.getTextChannel(of(webHook.getChannelId())).getAsMention());
      }
    }
    
    for (TextChannelDto textChannel : TextChannelDatabase.getAll(guildData.getId())) {
      for (String subredditName : textChannel.getSubreddits()) {
        subreddits.put(subredditName, guild.getTextChannel(of(textChannel.getId())).getAsMention());
      }
    }
  
    MessageEmbedDto content = new MessageEmbedDto();
    content.setTitle("Subreddit Feeds");
  
    PageDto subredditPage = new PageDto()
          .withContent(content);
    
    SiteDto subredditSite = new SiteDto()
          .withLabel("Subreddit Feeds")
          .withDescription("All subreddit feeds and their corresponding text channels.")
          .withPages(List.of(subredditPage));
    
    // Create a field for each subreddit
    subreddits.asMap().forEach((key, values) -> {
      String value = values.stream().reduce((u, v) -> u + StringUtils.LF + v).orElse(StringUtils.EMPTY);
      content.addField("r/" + key, value);
    });
    
    return subredditSite.getPages().isEmpty() ? Optional.empty() : Optional.of(subredditSite);
  }
  
  /**
   * <pre>
   *   +-----------------------+
   *   | Self-assignable Roles |
   *   +-----------------------+
   *   |                       |
   *   | &lt;Group&gt;         |
   *   |     @....             |
   *   |     @....             |
   *   |                       |
   *   +-----------------------+
   * </pre>
   *
   * @return A site over all self-assignable roles; Empty if none of the roles are self-assignable.
   * @throws SQLException If a database error occurred.
   */
  private Optional<SiteDto> createSelfAssignableRolesSite() throws SQLException {
    // Role Group -> Roles
    Multimap<String, String> roles = HashMultimap.create();
    
    for (RoleDto role : RoleDatabase.getAll(guildData.getId())) {
      roles.put(role.getGroup().orElse("default"), guild.getRole(of(guildData.getId())).getAsMention());
    }
  
    SiteDto roleSite = new SiteDto()
          .withLabel("Self-assignable Roles")
          .withDescription("All self-assignable roles.");
    
    roles.asMap().forEach((key, values) -> {
      String value = values.stream().reduce((u, v) -> u + StringUtils.LF + v).orElse(StringUtils.EMPTY);
  
      MessageEmbedDto content = new MessageEmbedDto();
      content.setTitle("Self-assignable Roles");
      content.addField(key, value);
      
      PageDto rolePage = new PageDto()
            .withContent(content);
  
      roleSite.getPages().add(rolePage);
    });
    
    return roleSite.getPages().isEmpty() ? Optional.empty() : Optional.of(roleSite);
  }
}
