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
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Message;
import zav.discord.blanc.api.Permission;
import zav.discord.blanc.api.site.SiteListener;
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

import java.sql.SQLException;
import java.util.*;

public class ConfigurationCommand extends AbstractGuildCommand implements SiteListener {
  private int index;
  private List<SiteValueObject> sites;
  private SiteValueObject currentSite;
  private PageValueObject currentPage;
  private GuildValueObject guildData;
  
  public ConfigurationCommand() {
    super(Permission.MANAGE_MESSAGES);
  }
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    Validate.validIndex(args, 0);
    guildData = guild.getAbout();
    sites = new ArrayList<>();
  }
  
  @Override
  public void run() throws SQLException {
    sites.add(createMainSite());
    sites.add(createBlacklistSite());
    sites.add(createSubredditSite());
    sites.add(createSelfAssignableRolesSite());
    
    channel.send(this, sites);
  }
  
  // ---------------------------------------------------------------------------------------------//
  //                                                                                              //
  //  Site Construction                                                                           //
  //                                                                                              //
  // ---------------------------------------------------------------------------------------------//
  
  
  private SiteValueObject createMainSite() {
    MessageEmbedValueObject content = new MessageEmbedValueObject();
    content.setTitle(guildData.getName());
    content.addField("Guild Prefix", guildData.getPrefix());
    
    PageValueObject mainPage = new PageValueObject()
          .withContent(content);
    
    return new SiteValueObject()
          .withPages(List.of(mainPage))
          .withLabel("mainPage")
          .withDescription("Main Page");
  }
    
  private SiteValueObject createBlacklistSite() {
    String value = guildData.getBlacklist()
          .stream()
          .reduce((u, v) -> u + StringUtils.LF + v)
          .orElse("");
  
    MessageEmbedValueObject content = new MessageEmbedValueObject();
    content.setTitle("Forbidden Expressions");
    content.setContent(value);
    
    PageValueObject blacklistPage = new PageValueObject()
          .withContent(content);
    
    return new SiteValueObject()
          .withPages(List.of(blacklistPage))
          .withLabel("blacklist")
          .withDescription("Forbidden Expressions");
  }

  private SiteValueObject createSubredditSite() throws SQLException {
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
  
    SiteValueObject subredditSite = new SiteValueObject()
          .withLabel("subreddits")
          .withDescription("Subreddit Feeds");
    
    // Create a page for each subreddit
    subreddits.asMap().forEach((key, values) -> {
      String value = values.stream().reduce((u, v) -> u + StringUtils.LF + v).orElse(StringUtils.EMPTY);
      
      MessageEmbedValueObject content = new MessageEmbedValueObject();
      content.setTitle("Subreddit Feeds");
      content.addField(key, value);
      
      PageValueObject subredditPage = new PageValueObject()
            .withContent(content);
      
      subredditSite.getPages().add(subredditPage);
    });
    
    return subredditSite;
  }

  private SiteValueObject createSelfAssignableRolesSite() throws SQLException {
    // Role Group -> Roles
    Multimap<String, String> roles = HashMultimap.create();
    
    for (RoleValueObject role : RoleDatabase.getAll(guildData.getId())) {
      roles.put(role.getGroup(), guild.getRole(of(guildData.getId())).getAsMention());
    }
  
    SiteValueObject roleSite = new SiteValueObject()
          .withLabel("roles")
          .withDescription("Self-assignable Roles");
    
    roles.asMap().forEach((key, values) -> {
      String value = values.stream().reduce((u, v) -> u + StringUtils.LF + v).orElse(StringUtils.EMPTY);
  
      MessageEmbedValueObject content = new MessageEmbedValueObject();
      content.setTitle("Self-assignable Roles");
      content.addField(key, value);
      
      PageValueObject rolePage = new PageValueObject()
            .withContent(content);
  
      roleSite.getPages().add(rolePage);
    });
    
    return roleSite;
  }
  
  // ---------------------------------------------------------------------------------------------//
  //                                                                                              //
  //  SiteListener                                                                                //
  //                                                                                              //
  // ---------------------------------------------------------------------------------------------//
  
  @Override
  public boolean canMoveLeft() {
    return index >= 0;
  }
  
  @Override
  public void moveLeft(Consumer<PageValueObject> consumer) {
    currentSite = sites.get(--index);
    currentPage = currentSite.getPages().get(0);
    
    consumer.accept(currentPage);
  }
  
  @Override
  public boolean canMoveRight() {
    return index < currentSite.getPages().size() - 1;
  }
  
  @Override
  public void moveRight(Consumer<PageValueObject> consumer) {
    currentSite = sites.get(++index);
    currentPage = currentSite.getPages().get(0);
  
    consumer.accept(currentPage);
  }
  
  @Override
  public void changeSelection(String label) {
    index = 0;
    
    currentSite = sites.stream()
          .filter(site -> site.getLabel().equals(label))
          .findFirst()
          .orElseThrow();
  }
}
