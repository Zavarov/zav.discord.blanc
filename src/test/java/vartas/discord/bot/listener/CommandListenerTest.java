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
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.*;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;
import vartas.discord.bot.Command;
import vartas.discord.bot.entities.Configuration;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.entities.offline.OfflineCommandBuilder;
import vartas.discord.bot.entities.offline.OfflineShard;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandListenerTest extends AbstractTest {
    static String GLOBAL_PREFIX = "global";
    static String GUILD_PREFIX = "local";
    OfflineShard shard;
    OfflineCommandBuilder builder;
    JDAImpl jda;
    UserImpl user;
    GuildImpl guild;
    TextChannelImpl channel;
    String prefixFreeMessageContent;

    Shard.CommandListener listener;
    MessageReceivedEvent event;
    AtomicBoolean flag;

    @Before
    public void setUp(){
        shard = OfflineShard.create(null);
        builder = shard.Builder;
        jda = new JDAImpl(Authorization);
        user = new SelfUserImpl(userId, jda);
        guild = new GuildImpl(jda, guildId);
        channel = new TextChannelImpl(channelId, guild){
            @Override
            protected void checkPermission(Permission permission){}
        };

        shard.guilds.put(guildId, guild);
        shard.accept(new Adder());

        prefixFreeMessageContent = "message";
        listener = shard.new CommandListener(GLOBAL_PREFIX);
        event = createEvent(GLOBAL_PREFIX);
        flag = new AtomicBoolean(false);
    }

    @Test
    public void onMessageReceivedTest(){
        listener.onMessageReceived(event);
        assertThat(flag).isTrue();
    }

    @Test
    public void onGuildMessageReceivedTest(){
        String prefix = GUILD_PREFIX;
        event = createEvent(prefix);

        listener.onMessageReceived(event);
        assertThat(flag).isTrue();
    }

    @Test
    public void onInvalidMessageReceivedTest(){
        builder.commands.clear();

        listener.onMessageReceived(event);
        assertThat(flag).isFalse();
        assertThat(shard.send).hasSize(1);
    }

    @Test
    public void onMessageReceivedIsBotTest(){
        user.setBot(true);

        listener.onMessageReceived(event);
        assertThat(flag).isFalse();
    }

    @Test
    public void onMessageReceivedErrorInParserTest(){
        event = createEvent(GLOBAL_PREFIX, (message, content) -> {
            throw new NullPointerException();
        });

        listener.onMessageReceived(event);
        assertThat(flag).isFalse();
        assertThat(shard.send).hasSize(1);
    }

    private static class Adder implements Shard.Visitor{
        @Override
        public void visit(@Nonnull Configuration configuration){
            configuration.setPrefix(GUILD_PREFIX);
        }
    }

    private static class Remover implements Shard.Visitor{
        @Override
        public void visit(@Nonnull Configuration configuration){
            configuration.removePrefix();
        }
    }

    private MessageReceivedEvent createEvent(String prefix){
        return createEvent(prefix, (message, content) -> flag.set(true));
    }

    private MessageReceivedEvent createEvent(String prefix, Command command){
        Message message = createMessage(prefix+"message");
        builder.commands.put(message, command);
        return new MessageReceivedEvent(jda, 54321L, message);
    }

    private DataMessage createMessage(String content){
        return new DataMessage(false, content, null, null){
            @Override
            protected void unsupported() {}
            @Nonnull
            @Override
            public TextChannel getTextChannel(){
                return channel;
            }
            @Nonnull
            @Override
            public UserImpl getAuthor(){
                return user;
            }
            @Nonnull
            @Override
            public GuildImpl getGuild(){
                return guild;
            }
            @Nonnull
            @Override
            public ChannelType getChannelType(){
                return getTextChannel().getType();
            }
            @Nonnull
            @Override
            public MessageChannel getChannel(){
                return getTextChannel();
            }
        };
    }
}
