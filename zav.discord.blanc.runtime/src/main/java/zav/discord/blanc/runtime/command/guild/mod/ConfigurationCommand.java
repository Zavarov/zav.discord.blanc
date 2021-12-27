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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Permission;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.databind.RoleValueObject;
import zav.discord.blanc.databind.TextChannelValueObject;
import zav.discord.blanc.databind.WebHookValueObject;
import zav.discord.blanc.databind.message.MessageEmbedValueObject;
import zav.discord.blanc.db.GuildDatabase;
import zav.discord.blanc.db.RoleDatabase;
import zav.discord.blanc.db.TextChannelDatabase;
import zav.discord.blanc.db.WebHookDatabase;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class ConfigurationCommand extends AbstractGuildCommand {
  private String myModule;
  private long guildId;
  
  public ConfigurationCommand() {
    super(Permission.MANAGE_MESSAGES);
  }
  
  @Override
  public void postConstruct(List<? extends Argument> args) {
    Validate.validIndex(args, 0);
    myModule = args.get(0).asString().orElseThrow().toLowerCase(Locale.ENGLISH);
    guildId = guild.getAbout().getId();
  }
  
  @Override
  public void run() throws SQLException {
    switch (myModule) {
      case "blacklist":
        showBlacklist();
        break;
      case "prefix":
        showPrefix();
        break;
      case "reddit":
        showSubredditFeeds();
        break;
      case "roles":
        showSelfAssignableRoles();
        break;
      default:
        channel.send("Unknown module: \"%s\"", myModule);
    }
  }
    
  private void showBlacklist() throws SQLException {
    MessageEmbedValueObject messageEmbed = new MessageEmbedValueObject();

    if (GuildDatabase.contains(guildId)) {
      String value = GuildDatabase.get(guildId)
            .getBlacklist()
            .stream()
            .reduce((u, v) -> u + "\n" + v).orElse("");
      messageEmbed.addField("Blacklist", value);
    }
    
    channel.send(messageEmbed);
  }

  private void showPrefix() throws SQLException {
    MessageEmbedValueObject messageEmbed = new MessageEmbedValueObject();

    if (GuildDatabase.contains(guildId)) {
      String value = GuildDatabase.get(guildId).getPrefix();
      messageEmbed.addField("Prefix", value != null ? value : StringUtils.EMPTY);
    }
    
    channel.send(messageEmbed);
  }

  private void showSubredditFeeds() throws SQLException {
    MessageEmbedValueObject messageEmbed = new MessageEmbedValueObject();
  
    for (WebHookValueObject webHook : WebHookDatabase.getAll(guildId)) {
      String value = webHook.getSubreddits().stream().reduce((u, v) -> u + "\n" + v).orElse("");
      //Only print channels that link to at least one subreddit
      if (!value.isBlank()) {
        messageEmbed.addField(webHook.getName(), value);
      }
    }
  
    for (TextChannelValueObject channel : TextChannelDatabase.getAll(guildId)) {
      String value = channel.getSubreddits().stream().reduce((u, v) -> u + "\n" + v).orElse("");
      //Only print channels that link to at least one subreddit
      if (!value.isBlank()) {
        messageEmbed.addField(channel.getName(), value);
      }
    }

    channel.send(messageEmbed);
  }

  private void showSelfAssignableRoles() throws SQLException {
    MessageEmbedValueObject messageEmbed = new MessageEmbedValueObject();

    for (RoleValueObject role : RoleDatabase.getAll(guildId)) {
      if (role.getGroup() != null) {
        messageEmbed.addField(role.getName(), role.getGroup());
      }
    }

    channel.send(messageEmbed);
  }
}
