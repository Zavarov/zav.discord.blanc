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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.RestAction;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebhookEntity;
import zav.discord.blanc.runtime.command.AbstractDatabaseTest;

/**
 * This test case verifies whether the list of all currently registered Reddit feeds is correctly
 * displayed.
 */
@ExtendWith(MockitoExtension.class)
public class RedditConfigurationCommandTest extends AbstractDatabaseTest<GuildEntity> {
  @Captor ArgumentCaptor<List<Site.Page>> captor;
  @Mock InteractionHook hook;
  @Mock RestAction<Message> action;
  @Mock Message message;
  @Mock Member member;
  @Mock Guild guild;
  @Mock TextChannel channel;
  @Mock SlashCommandEvent event;
  
  TextChannelEntity channelEntity;
  WebhookEntity webhookEntity;
  GuildCommandManager manager;
  RedditConfigurationCommand command;
  
  /**
   * Initializes the command with no arguments. The database is initialized with one webhook and one
   * text channel entity, both registered to the subreddit {@code RedditDev}.
   */
  @BeforeEach
  public void setUp() {
    super.setUp(new GuildEntity());
    when(event.getMember()).thenReturn(member);
    when(event.getGuild()).thenReturn(guild);
    when(entityManager.find(eq(GuildEntity.class), any())).thenReturn(entity);
    
    manager = spy(new GuildCommandManager(client, event));
    command = new RedditConfigurationCommand(event, manager);
    webhookEntity = new WebhookEntity();
    webhookEntity.setSubreddits(Lists.newArrayList("RedditDev"));
    channelEntity = new TextChannelEntity();
    channelEntity.setSubreddits(Lists.newArrayList("RedditDev", "BoatsOnWheels"));
    channelEntity.add(webhookEntity);
    entity.add(channelEntity);
    entity.add(webhookEntity);
    
    doNothing().when(manager).submit(captor.capture());
  }
  
  @Test
  public void testShowEmptyPage() throws Exception {
    entity.setTextChannels(Lists.newArrayList());
    entity.setWebhooks(Lists.newArrayList());
    
    command.run();
  
    assertThat(captor.getValue()).isEmpty();
  }
  
  @Test
  public void testShowPageWithWebhooks() throws Exception {
    when(guild.getTextChannelById(anyLong())).thenReturn(channel);
    when(channel.getAsMention()).thenReturn("@TextChannel");
    entity.setTextChannels(Lists.newArrayList());
    entity.setWebhooks(Lists.newArrayList(webhookEntity));
    
    command.run();
  
    assertThat(captor.getValue()).hasSize(1);
  }
  
  @Test
  public void testShowPageWithTextChannels() throws Exception {
    when(guild.getTextChannelById(anyLong())).thenReturn(channel);
    when(channel.getAsMention()).thenReturn("@TextChannel");
    entity.setTextChannels(Lists.newArrayList(channelEntity));
    entity.setWebhooks(Lists.newArrayList());
    
    command.run();
  
    assertThat(captor.getValue()).hasSize(1);
  }
  
  @Test
  public void testShowPageWithUnknownTextChannel() throws Exception {
    entity.setTextChannels(Lists.newArrayList(channelEntity));
    entity.setWebhooks(Lists.newArrayList(webhookEntity));
    
    command.run();

    assertThat(captor.getValue()).isEmpty();
  }
  
  @Test
  public void testShowPageWithMultipleChannels() throws Exception {
    when(guild.getTextChannelById(anyLong())).thenReturn(channel);
    when(channel.getAsMention()).thenReturn("@TextChannel");
    entity.setTextChannels(Lists.newArrayList(channelEntity));
    entity.setWebhooks(Lists.newArrayList(webhookEntity));
    
    command.run();
  
    assertThat(captor.getValue()).hasSize(1);
  }
}
