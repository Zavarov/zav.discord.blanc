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
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.databind.RoleValueObject;
import zav.discord.blanc.databind.TextChannelValueObject;
import zav.discord.blanc.databind.WebHookValueObject;
import zav.discord.blanc.databind.message.MessageEmbedValueObject;
import zav.discord.blanc.databind.message.PageValueObject;
import zav.discord.blanc.databind.message.SiteValueObject;
import zav.discord.blanc.db.RoleDatabase;
import zav.discord.blanc.db.TextChannelDatabase;
import zav.discord.blanc.db.WebHookDatabase;

public class ConfigurationCommand extends AbstractGuildCommand {
  private GuildValueObject guildData;
  
  public ConfigurationCommand() {
    super(Permission.MANAGE_MESSAGES);
  }
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    guildData = guild.getAbout();
  }
  
  @Override
  public void run() throws SQLException {
    List<SiteValueObject> sites = new ArrayList<>();
    
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
  private SiteValueObject createMainSite() {
    MessageEmbedValueObject content = new MessageEmbedValueObject();
    content.setTitle(guildData.getName());
    content.addField("Guild Prefix", guildData.getPrefix().orElse("<None>"));
    content.addField("Active User", author.getAsMention());
    
    PageValueObject mainPage = new PageValueObject()
          .withContent(content);
    
    return new SiteValueObject()
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
  private Optional<SiteValueObject> createBlacklistSite() {
    if (guildData.getBlacklist().isEmpty()) {
      return Optional.empty();
    }
    
    String value = guildData.getBlacklist()
          .stream()
          .reduce((u, v) -> u + StringUtils.LF + v)
          .orElse("");
  
    MessageEmbedValueObject content = new MessageEmbedValueObject();
    content.setTitle("Forbidden Expressions");
    content.setContent(value);
    
    PageValueObject blacklistPage = new PageValueObject()
          .withContent(content);
    
    SiteValueObject site = new SiteValueObject()
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
  private Optional<SiteValueObject> createSubredditSite() throws SQLException {
    // Subreddit Name -> Text Channels
    Multimap<String, String> subreddits = HashMultimap.create();
    
    // Get all subreddit feeds
    for (WebHookValueObject webHook : WebHookDatabase.getAll(guildData.getId())) {
      for (String subredditName : webHook.getSubreddits()) {
        subreddits.put(subredditName, guild.getTextChannel(of(webHook.getChannelId())).getAsMention());
      }
    }
    
    for (TextChannelValueObject textChannel : TextChannelDatabase.getAll(guildData.getId())) {
      for (String subredditName : textChannel.getSubreddits()) {
        subreddits.put(subredditName, guild.getTextChannel(of(textChannel.getId())).getAsMention());
      }
    }
  
    MessageEmbedValueObject content = new MessageEmbedValueObject();
    content.setTitle("Subreddit Feeds");
  
    PageValueObject subredditPage = new PageValueObject()
          .withContent(content);
    
    SiteValueObject subredditSite = new SiteValueObject()
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
  private Optional<SiteValueObject> createSelfAssignableRolesSite() throws SQLException {
    // Role Group -> Roles
    Multimap<String, String> roles = HashMultimap.create();
    
    for (RoleValueObject role : RoleDatabase.getAll(guildData.getId())) {
      roles.put(role.getGroup().orElse("default"), guild.getRole(of(guildData.getId())).getAsMention());
    }
  
    SiteValueObject roleSite = new SiteValueObject()
          .withLabel("Self-assignable Roles")
          .withDescription("All self-assignable roles.");
    
    roles.asMap().forEach((key, values) -> {
      String value = values.stream().reduce((u, v) -> u + StringUtils.LF + v).orElse(StringUtils.EMPTY);
  
      MessageEmbedValueObject content = new MessageEmbedValueObject();
      content.setTitle("Self-assignable Roles");
      content.addField(key, value);
      
      PageValueObject rolePage = new PageValueObject()
            .withContent(content);
  
      roleSite.getPages().add(rolePage);
    });
    
    return roleSite.getPages().isEmpty() ? Optional.empty() : Optional.of(roleSite);
  }
}
