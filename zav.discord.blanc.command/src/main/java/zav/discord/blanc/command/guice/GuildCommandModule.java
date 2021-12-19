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

package zav.discord.blanc.command.guice;

import com.google.inject.AbstractModule;
import zav.discord.blanc.api.Guild;
import zav.discord.blanc.api.GuildMessage;
import zav.discord.blanc.api.Member;
import zav.discord.blanc.api.Message;
import zav.discord.blanc.api.MessageChannel;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.api.TextChannel;
import zav.discord.blanc.api.User;

/**
 * Injector module for all guild commands.<br>
 * It prepares the following classes for injection:
 * <pre>
 *   - MemberView
 *   - UserView
 *   - TextChannelView
 *   - MessageChannelView
 *   - GuildView
 *   - GuildMessageView
 *   - MessageView
 *   - ShardView
 * </pre>
 */
public class GuildCommandModule extends AbstractModule {
  private final GuildMessage msg;
  
  public GuildCommandModule(GuildMessage msg) {
    this.msg = msg;
  }
  
  @Override
  protected void configure() {
    // AbstractGuildCommand
    bind(Member.class).toInstance(msg.getAuthor());
    bind(TextChannel.class).toInstance(msg.getMessageChannel());
    bind(Guild.class).toInstance(msg.getGuild());
    bind(GuildMessage.class).toInstance(msg);
  
    // AbstractCommand
    bind(Shard.class).toInstance(msg.getShard());
    bind(User.class).toInstance(msg.getAuthor());
    bind(MessageChannel.class).toInstance(msg.getMessageChannel());
    bind(Message.class).toInstance(msg);
  }
}
