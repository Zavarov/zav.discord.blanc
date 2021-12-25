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

package zav.discord.blanc.runtime.internal.guice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import zav.discord.blanc.command.parser.Parser;
import zav.discord.blanc.databind.io.CredentialsValueObject;
import zav.discord.blanc.mc.MontiCoreCommandParser;

import java.io.IOException;

public class BlancModule extends AbstractModule {
  private final CredentialsValueObject credentials;
  
  public BlancModule() {
    try {
      ObjectMapper om = new ObjectMapper();
      credentials = om.readValue(BlancModule.class.getClassLoader().getResourceAsStream("BlancCredentials.json"), CredentialsValueObject.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  @Override
  protected void configure() {
    bind(String.class).annotatedWith(Names.named("botName")).toInstance(credentials.getBotName());
    bind(String.class).annotatedWith(Names.named("globalPrefix")).toInstance(credentials.getGlobalPrefix());
    bind(Long.class).annotatedWith(Names.named("shardCount")).toInstance(credentials.getShardCount());
    bind(String.class).annotatedWith(Names.named("inviteSupportServer")).toInstance(credentials.getInviteSupportServer());
    bind(String.class).annotatedWith(Names.named("wikiUrl")).toInstance(credentials.getWikiUrl());
    bind(String.class).annotatedWith(Names.named("discordToken")).toInstance(credentials.getDiscordToken());
    bind(Long.class).annotatedWith(Names.named("owner")).toInstance(credentials.getOwner());
    
    bind(Parser.class).to(MontiCoreCommandParser.class);
  }
}
