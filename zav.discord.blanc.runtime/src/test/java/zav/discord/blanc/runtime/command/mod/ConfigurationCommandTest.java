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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.db.GuildTable;
import zav.discord.blanc.runtime.command.AbstractCommandTest;
import zav.test.io.JsonUtils;

@ExtendWith(MockitoExtension.class)
public class ConfigurationCommandTest  extends AbstractCommandTest {
  private @Mock MessageAction action;
  
  private GuildTable guildTable;
  private GuildEntity guildEntity;
  
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    
    guildEntity = JsonUtils.read("Guild.json", GuildEntity.class);
    guildTable = injector.getInstance(GuildTable.class);
  }
  
  @Test
  public void testCommandIsOfCorrectType() {
    check("b:mod.config", ConfigurationCommand.class);
  }
  
  @Test
  public void testRun() throws Exception {
    when(member.getAsMention()).thenReturn("author");
    when(textChannel.sendMessage(any(Message.class))).thenReturn(action);

    doAnswer(answer -> {
      Consumer<Message> success = answer.getArgument(0);
      success.accept(mock(Message.class));
      return null;
    }).when(action).queue(any());
    
    guildTable.put(guildEntity);
    
    run("b:mod.config");
    
    assertThat(siteCache.size()).isNotZero();
  }
}
