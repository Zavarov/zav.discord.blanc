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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.function.Consumer;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.RestAction;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import zav.discord.blanc.runtime.command.AbstractGuildCommandTest;

public class SubredditFeedConfigurationCommandTest extends AbstractGuildCommandTest {
  private @Mock InteractionHook hook;
  private @Mock RestAction<Message> action;
  
  @Test
  public void testRun() throws Exception {
    update(webhookTable, webhookEntity, e -> {});
    when(guild.getIdLong()).thenReturn(guildEntity.getId());
    when(guild.getTextChannelById(anyLong())).thenReturn(textChannel);
    when(textChannel.getAsMention()).thenReturn(channelEntity.getName());
    when(event.replyEmbeds(any(MessageEmbed.class))).thenReturn(reply);
    
    doAnswer(answer -> {
      Consumer<InteractionHook> success = answer.getArgument(0);
      when(hook.retrieveOriginal()).thenReturn(action);
      when(action.complete()).thenReturn(mock(Message.class));
      success.accept(hook);
      return null;
    }).when(reply).queue(any());
    
    run(SubredditFeedConfigurationCommand.class);
    
    assertThat(siteCache.size()).isNotZero();
  }
}
