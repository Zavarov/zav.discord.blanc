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

package zav.discord.blanc.jda;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import org.junit.jupiter.api.BeforeEach;

import java.util.EnumSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

@SuppressWarnings("all")
public abstract class AbstractTest {
  protected final long roleId = 11111;
  protected final long memberId = 22222;
  protected final long textChannelId = 33333;
  protected final long messageId = 44444;
  protected final long guildId = 55555;
  protected final long userId = 66666;
  protected final String webHookName = "webHook";
  protected final String jdaToken = "token";
  protected final EnumSet<Permission> memberPermissions = EnumSet.of(Permission.MESSAGE_MANAGE);
  
  protected JDA jda;
  protected PrivateChannel jdaPrivateChannel;
  protected MessageChannel jdaChannel;
  protected RestAction<Message> jdaMessageAction;
  protected Message jdaMessage;
  protected RestAction<User> jdaUserAction;
  protected User jdaUser;
  
  protected Guild jdaGuild;
  protected TextChannel jdaTextChannel;
  protected Member jdaSelfMember;
  protected Member jdaMember;
  protected Role jdaRole;
  protected RestAction<List<Webhook>> jdaWebHooks;
  protected WebhookAction jdaWebHookAction;
  protected Webhook jdaWebHook;
  
  @BeforeEach
  public void setUpMocks() {
    jda = mock(JDA.class);
    jdaPrivateChannel = mock(PrivateChannel.class);
    jdaMessageAction = mock(RestAction.class);
    jdaMessage = mock(Message.class);
    jdaUserAction = mock(RestAction.class);
    jdaUser = mock(User.class);
    
    jdaGuild = mock(Guild.class);
    jdaChannel = mock(MessageChannel.class);
    jdaTextChannel = mock(TextChannel.class);
    jdaSelfMember = mock(Member.class);
    jdaMember = mock(Member.class);
    jdaRole = mock(Role.class);
    jdaWebHook = mock(Webhook.class);
    jdaWebHooks = mock(RestAction.class);
    jdaWebHookAction = mock(WebhookAction.class);
    
    when(jda.getSelfUser()).thenReturn(mock(SelfUser.class));
    when(jda.getGuilds()).thenReturn(List.of(mock(Guild.class)));
    when(jda.getGuildById(eq(guildId))).thenReturn(mock(Guild.class));
    when(jda.retrieveUserById(eq(userId))).thenReturn(jdaUserAction);
    
    when(jdaPrivateChannel.retrieveMessageById(eq(messageId))).thenReturn(jdaMessageAction);
  
    when(jdaMessageAction.complete()).thenReturn(jdaMessage);
  
    when(jdaMessage.getAuthor()).thenReturn(jdaUser);
    when(jdaMessage.getMember()).thenReturn(jdaMember);
    when(jdaMessage.getGuild()).thenReturn(jdaGuild);
    when(jdaMessage.getChannel()).thenReturn(jdaChannel);
    when(jdaMessage.getTextChannel()).thenReturn(jdaTextChannel);
    when(jdaMessage.getPrivateChannel()).thenReturn(jdaPrivateChannel);
    when(jdaMessage.getJDA()).thenReturn(jda);
    
    when(jdaUserAction.complete()).thenReturn(jdaUser);
  
    when(jdaGuild.getSelfMember()).thenReturn(jdaSelfMember);
    when(jdaGuild.getRoleById(eq(roleId))).thenReturn(jdaRole);
    when(jdaGuild.getMemberById(eq(memberId))).thenReturn(jdaMember);
    when(jdaGuild.getTextChannelById(eq(textChannelId))).thenReturn(jdaTextChannel);
    when(jdaGuild.getRoles()).thenReturn(List.of(jdaRole));
    when(jdaGuild.getMembers()).thenReturn(List.of(jdaMember, jdaSelfMember));
    when(jdaGuild.getTextChannels()).thenReturn(List.of(jdaTextChannel));
  
    when(jdaTextChannel.retrieveMessageById(eq(messageId))).thenReturn(jdaMessageAction);
    when(jdaTextChannel.retrieveWebhooks()).thenReturn(jdaWebHooks);
    when(jdaTextChannel.createWebhook(eq(webHookName))).thenReturn(jdaWebHookAction);
    
    when(jdaSelfMember.getUser()).thenReturn(jdaUser);
    
    when(jdaMember.getUser()).thenReturn(jdaUser);
    when(jdaMember.getRoles()).thenReturn(List.of(jdaRole));
    when(jdaMember.getPermissions()).thenReturn(memberPermissions);
    
    when(jdaWebHook.getName()).thenReturn(webHookName);
    when(jdaWebHook.getToken()).thenReturn(jdaToken);
    when(jdaWebHook.getJDA()).thenReturn(jda);
    when(jdaWebHooks.complete()).thenReturn(List.of(jdaWebHook));
    when(jdaWebHookAction.complete()).thenReturn(jdaWebHook);
    
  }
}
