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

package zav.discord.blanc.runtime.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static zav.test.io.JsonUtils.read;

import com.google.inject.Guice;
import java.util.EnumSet;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildChannel;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import zav.discord.blanc.api.guice.GuildCommandModule;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebHookEntity;
import zav.discord.blanc.db.GuildTable;
import zav.discord.blanc.db.TextChannelTable;
import zav.discord.blanc.db.WebHookTable;

public abstract class AbstractGuildCommandTest extends AbstractTest {
  protected @Mock JDA jda;
  protected @Mock Guild guild;
  protected @Mock TextChannel textChannel;
  protected @Mock User user;
  protected @Mock Member member;
  protected @Mock SlashCommandEvent event;
  protected @Mock ReplyAction reply;
  
  protected GuildEntity guildEntity;
  protected GuildTable guildTable;
  
  protected WebHookEntity webhookEntity;
  protected WebHookTable webhookTable;
  
  protected TextChannelEntity channelEntity;
  protected TextChannelTable channelTable;
  
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    
    when(event.getJDA()).thenReturn(jda);
    when(event.getChannel()).thenReturn(textChannel);
    when(event.getUser()).thenReturn(user);
    when(event.getGuild()).thenReturn(guild);
    when(event.getTextChannel()).thenReturn(textChannel);
    when(event.getMember()).thenReturn(member);
    when(member.getUser()).thenReturn(user);
    when(member.getPermissions(any(GuildChannel.class))).thenReturn(EnumSet.allOf(Permission.class));
    injector = Guice.createInjector(new GuildCommandModule(event), new TestModule());
    
    guildEntity = read("Guild.json", GuildEntity.class);
    guildTable = injector.getInstance(GuildTable.class);
    guildTable.put(guildEntity);
    webhookEntity = read("WebHook.json", WebHookEntity.class);
    webhookTable = injector.getInstance(WebHookTable.class);
    webhookTable.put(webhookEntity);
    channelEntity = read("TextChannel.json", TextChannelEntity.class);
    channelTable = injector.getInstance(TextChannelTable.class);
    channelTable.put(channelEntity);
  }
}
