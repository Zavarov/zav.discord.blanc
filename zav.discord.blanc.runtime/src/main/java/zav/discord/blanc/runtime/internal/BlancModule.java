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

package zav.discord.blanc.runtime.internal;

import static zav.discord.blanc.api.Constants.BOT_NAME;
import static zav.discord.blanc.api.Constants.DISCORD_TOKEN;
import static zav.discord.blanc.api.Constants.GLOBAL_PREFIX;
import static zav.discord.blanc.api.Constants.INVITE_SUPPORT_SERVER;
import static zav.discord.blanc.api.Constants.OWNER;
import static zav.discord.blanc.api.Constants.SHARD_COUNT;
import static zav.discord.blanc.api.Constants.WIKI_URL;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import zav.discord.blanc.api.Parser;
import zav.discord.blanc.databind.io.CredentialsEntity;
import zav.discord.blanc.mc.MontiCoreCommandParser;

public class BlancModule extends AbstractModule {
  private static final ScheduledExecutorService QUEUE = Executors.newScheduledThreadPool(16);
  private final CredentialsEntity credentials;
  
  public BlancModule(CredentialsEntity credentials) {
    this.credentials = credentials;
  }
  
  @Override
  protected void configure() {
    bind(String.class).annotatedWith(Names.named(BOT_NAME)).toInstance(credentials.getBotName());
    bind(String.class).annotatedWith(Names.named(GLOBAL_PREFIX)).toInstance(credentials.getGlobalPrefix());
    bind(Long.class).annotatedWith(Names.named(SHARD_COUNT)).toInstance(credentials.getShardCount());
    bind(String.class).annotatedWith(Names.named(INVITE_SUPPORT_SERVER)).toInstance(credentials.getInviteSupportServer());
    bind(String.class).annotatedWith(Names.named(WIKI_URL)).toInstance(credentials.getWikiUrl());
    bind(String.class).annotatedWith(Names.named(DISCORD_TOKEN)).toInstance(credentials.getDiscordToken());
    bind(Long.class).annotatedWith(Names.named(OWNER)).toInstance(credentials.getOwner());
    
    bind(Parser.class).to(MontiCoreCommandParser.class);
    bind(ExecutorService.class).toInstance(QUEUE);
    bind(ScheduledExecutorService.class).toInstance(QUEUE);
  }
}
