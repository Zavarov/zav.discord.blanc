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

import static com.google.inject.name.Names.named;
import static zav.discord.blanc.api.Constants.BOT_NAME;
import static zav.discord.blanc.api.Constants.DISCORD_TOKEN;
import static zav.discord.blanc.api.Constants.GLOBAL_PREFIX;
import static zav.discord.blanc.api.Constants.INVITE_SUPPORT_SERVER;
import static zav.discord.blanc.api.Constants.OWNER;
import static zav.discord.blanc.api.Constants.SHARD_COUNT;
import static zav.discord.blanc.api.Constants.WIKI_URL;

import com.google.inject.AbstractModule;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import zav.discord.blanc.api.Parser;
import zav.discord.blanc.databind.io.CredentialsEntity;
import zav.discord.blanc.mc.MontiCoreCommandParser;

/**
 * Root module of the application.
 */
public class BlancModule extends AbstractModule {
  private static final ScheduledExecutorService QUEUE = Executors.newScheduledThreadPool(16);
  
  private final String botName;
  private final String globalPrefix;
  private final long shardCount;
  private final String inviteSupportServer;
  private final String wikiUrl;
  private final String discordToken;
  private final long owner;
  
  /**
   * Initializes all member variables with the elements stored in the credentials.
   *
   * @param credentials The user credentials.
   */
  public BlancModule(CredentialsEntity credentials) {
    botName = credentials.getBotName();
    globalPrefix = credentials.getGlobalPrefix();
    shardCount = credentials.getShardCount();
    inviteSupportServer = credentials.getInviteSupportServer();
    wikiUrl = credentials.getWikiUrl();
    discordToken = credentials.getDiscordToken();
    owner = credentials.getOwner();
  }
  
  @Override
  protected void configure() {
    bind(String.class).annotatedWith(named(BOT_NAME)).toInstance(botName);
    bind(String.class).annotatedWith(named(GLOBAL_PREFIX)).toInstance(globalPrefix);
    bind(Long.class).annotatedWith(named(SHARD_COUNT)).toInstance(shardCount);
    bind(String.class).annotatedWith(named(INVITE_SUPPORT_SERVER)).toInstance(inviteSupportServer);
    bind(String.class).annotatedWith(named(WIKI_URL)).toInstance(wikiUrl);
    bind(String.class).annotatedWith(named(DISCORD_TOKEN)).toInstance(discordToken);
    bind(Long.class).annotatedWith(named(OWNER)).toInstance(owner);
    
    bind(Parser.class).to(MontiCoreCommandParser.class);
    bind(ExecutorService.class).toInstance(QUEUE);
    bind(ScheduledExecutorService.class).toInstance(QUEUE);
  }
}
