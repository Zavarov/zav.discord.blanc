/*
 * Copyright (c) 2019 Zavarov
 *
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

package vartas.discord.bot.listener;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.*;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;
import vartas.discord.bot.entities.offline.OfflineShard;
import vartas.discord.bot.message.InteractiveMessage;
import vartas.discord.bot.message.builder.InteractiveMessageBuilder;

import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThat;

public class InteractiveMessageListenerTest extends AbstractTest {
    static int LIFETIME = 12;
    OfflineShard shard;
    JDAImpl jda;
    SelfUserImpl user;
    RoleImpl role;
    GuildImpl guild;
    MemberImpl member;
    TextChannelImpl textChannel;
    PrivateChannelImpl privateChannel;
    Message message;

    InteractiveMessageListener listener;
    MessageReactionAddEvent event;
    InteractiveMessage interactiveMessage;
    MessageEmbed firstPage;
    @Before
    public void setUp(){
        shard = OfflineShard.create(null);
        jda = new JDAImpl(Authorization);
        user = new SelfUserImpl(userId, jda);
        guild = new GuildImpl(jda, guildId){
            @Override
            public Member getMember(@Nonnull User user){
                return member;
            }
        };
        member = new MemberImpl(guild, user);
        textChannel = new TextChannelImpl(channelId, guild){
            @Override
            protected void checkPermission(Permission permission){}
        };
        role = new RoleImpl(roleId, guild);
        privateChannel = new PrivateChannelImpl(channelId, user);
        message = createMessage("message", textChannel);

        jda.setSelfUser(user);
        guild.setOwner(member);
        guild.setPublicRole(role);

        listener = new InteractiveMessageListener(LIFETIME);
        interactiveMessage = new InteractiveMessageBuilder(user, shard).nextPage().nextPage().build();
        firstPage = interactiveMessage.build();
        event = createEvent(message, textChannel);

        listener.add(message, interactiveMessage);
    }

    @Test
    public void onMessageReactionAddIsBotTest(){
        user.setBot(true);

        listener.onMessageReactionAdd(event);
        assertThat(interactiveMessage.build()).isEqualTo(firstPage);
    }

    @Test
    public void onMessageReactionAddInPrivateChannelTest(){
        event = createEvent(message, privateChannel);

        listener.onMessageReactionAdd(event);
        assertThat(interactiveMessage.build()).isNotEqualTo(firstPage);
    }

    @Test
    public void onMessageReactionAddInTextChannelTest(){
        listener.onMessageReactionAdd(event);
        assertThat(interactiveMessage.build()).isNotEqualTo(firstPage);
    }

    private MessageReactionAddEvent createEvent(Message message, MessageChannel channel){
        MessageReaction messageReaction = new MessageReaction(channel, MessageReaction.ReactionEmote.fromUnicode("\u2b05", jda), message.getIdLong(), true, 1);
        return new MessageReactionAddEvent(jda, 54321L, user, member, messageReaction, userId);
    }

    private DataMessage createMessage(String content, MessageChannel channel){
        return new DataMessage(false, content, null, null){
            @Override
            protected void unsupported() {}
            @Nonnull
            @Override
            public TextChannelImpl getTextChannel(){
                return (TextChannelImpl) channel;
            }
            @Nonnull
            @Override
            public UserImpl getAuthor(){
                return user;
            }
            @Nonnull
            @Override
            public GuildImpl getGuild(){
                return getTextChannel().getGuild();
            }
            @Nonnull
            @Override
            public ChannelType getChannelType(){
                return getChannel().getType();
            }
            @Nonnull
            @Override
            public MessageChannel getChannel(){
                return channel;
            }
            @Override
            public long getIdLong(){
                return 12345L;
            }
        };
    }
}
