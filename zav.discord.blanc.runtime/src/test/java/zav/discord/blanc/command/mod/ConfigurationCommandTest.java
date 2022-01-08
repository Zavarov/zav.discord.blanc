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

package zav.discord.blanc.command.mod;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import zav.discord.blanc.command.AbstractCommandTest;
import zav.discord.blanc.command.Command;
import zav.discord.blanc.databind.message.FieldDto;
import zav.discord.blanc.databind.message.MessageEmbedDto;
import zav.discord.blanc.db.GuildDatabase;
import zav.discord.blanc.db.RoleDatabase;
import zav.discord.blanc.db.TextChannelDatabase;
import zav.discord.blanc.db.WebHookDatabase;
import zav.discord.blanc.runtime.command.mod.ConfigurationCommand;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ConfigurationCommandTest  extends AbstractCommandTest {
  private Command command;
  
  @BeforeEach
  public void setUp() throws SQLException {
    command = parse("b:mod.config blacklist");
  
    guildDto.getBlacklist().add("foo");
    guildDto.setPrefix("bar");
    GuildDatabase.put(guildDto);
  
    TextChannelDatabase.put(guildDto, channelDto);
    WebHookDatabase.put(guildDto, channelDto, webHookDto);
    RoleDatabase.put(guildDto, roleDto);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    assertThat(command).isInstanceOf(ConfigurationCommand.class);
  }
  
  @Test
  public void testGetBlacklist() throws Exception {
    command = parse("b:mod.config blacklist");
    
    command.run();
  
    ArgumentCaptor<MessageEmbedDto> msgCaptor = ArgumentCaptor.forClass(MessageEmbedDto.class);
    verify(channelView, times(1)).send(msgCaptor.capture());
  
    MessageEmbedDto msg = msgCaptor.getValue();
    assertThat(msg.getFields()).hasSize(1);
    
    FieldDto field = msg.getFields().get(0);
    assertThat(field.getName()).isEqualTo("Blacklist");
    assertThat(field.getContent()).isEqualTo("foo");
  }
  
  @Test
  public void testGetPrefix() throws Exception {
    command = parse("b:mod.config prefix");
  
    command.run();
  
    ArgumentCaptor<MessageEmbedDto> msgCaptor = ArgumentCaptor.forClass(MessageEmbedDto.class);
    verify(channelView, times(1)).send(msgCaptor.capture());
  
    MessageEmbedDto msg = msgCaptor.getValue();
    assertThat(msg.getFields()).hasSize(1);
  
    FieldDto field = msg.getFields().get(0);
    assertThat(field.getName()).isEqualTo("Prefix");
    assertThat(field.getContent()).isEqualTo("bar");
  }
  
  @Test
  public void testGetSubreddits() throws Exception {
    command = parse("b:mod.config reddit");
  
    command.run();
  
    ArgumentCaptor<MessageEmbedDto> msgCaptor = ArgumentCaptor.forClass(MessageEmbedDto.class);
    verify(channelView, times(1)).send(msgCaptor.capture());
  
    MessageEmbedDto msg = msgCaptor.getValue();
    assertThat(msg.getFields()).hasSize(2);
  
    FieldDto field = msg.getFields().get(0);
    assertThat(field.getName()).isEqualTo(webHookName);
    assertThat(field.getContent()).isEqualTo(webHookSubreddit);
  
    field = msg.getFields().get(1);
    assertThat(field.getName()).isEqualTo(channelName);
    assertThat(field.getContent()).isEqualTo(channelSubreddit);
  
  }
  
  @Test
  public void testGetRoles() throws Exception {
    command = parse("b:mod.config roles");
  
    command.run();
  
    ArgumentCaptor<MessageEmbedDto> msgCaptor = ArgumentCaptor.forClass(MessageEmbedDto.class);
    verify(channelView, times(1)).send(msgCaptor.capture());
  
    MessageEmbedDto msg = msgCaptor.getValue();
    assertThat(msg.getFields()).hasSize(1);
  
    FieldDto field = msg.getFields().get(0);
    assertThat(field.getName()).isEqualTo(roleName);
    assertThat(field.getContent()).isEqualTo(roleDto.getGroup());
  
  }
  
  @Test
  public void testGetUnknownModule() throws Exception {
    command = parse("b:mod.config foo");
  
    command.run();
  
    ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> moduleCaptor = ArgumentCaptor.forClass(String.class);
    verify(channelView, times(1)).send(msgCaptor.capture(), moduleCaptor.capture());
  
    assertThat(msgCaptor.getValue()).isEqualTo("Unknown module: \"%s\"");
    assertThat(moduleCaptor.getValue()).isEqualTo("foo");
  }
}
