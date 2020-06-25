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

package vartas.discord.listener;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.JDAImpl;
import net.dv8tion.jda.internal.entities.*;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.AbstractTest;
import vartas.discord.entities.Configuration;
import vartas.discord.entities.Shard;
import vartas.discord.entities.offline.OfflineShard;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class BlacklistListenerTest extends AbstractTest {
    OfflineShard shard;
    JDAImpl jda;
    SelfUserImpl user;
    GuildImpl guild;
    TextChannelImpl channel;
    Shard.BlacklistListener listener;
    GuildMessageReceivedEvent event;
    @Before
    public void setUp(){
        shard = OfflineShard.create(null);
        jda = new JDAImpl(Authorization);
        user = new SelfUserImpl(userId, jda);
        guild = new GuildImpl(jda, guildId);
        channel = new TextChannelImpl(channelId, guild);

        shard.guilds.put(guildId, guild);
        shard.accept(new Adder());

        listener = shard.new BlacklistListener();
        event = new GuildMessageReceivedEvent(jda, 54321L, createMessage("pattern"));
        jda.setSelfUser(new SelfUserImpl(50, jda));
    }

    @Test
    public void onGuildMessageReceivedFromItselfTest(){
        jda.setSelfUser(user);

        assertThat(shard.send).isEmpty();
        listener.onGuildMessageReceived(event);
        assertThat(shard.send).isEmpty();
    }

    @Test
    public void onGuildMessageReceivedMatchTest(){
        assertThat(shard.send).isEmpty();
        listener.onGuildMessageReceived(event);
        assertThat(shard.send).hasSize(1);
    }

    @Test
    public void onGuildMessageReceivedNoMatchTest(){
        event = new GuildMessageReceivedEvent(jda, 54321L, createMessage("junk"));

        assertThat(shard.send).isEmpty();
        listener.onGuildMessageReceived(event);
        assertThat(shard.send).isEmpty();
    }

    @Test
    public void onGuildMessageReceivedNoPatternTest(){
        shard.accept(new Remover());

        assertThat(shard.send).isEmpty();
        listener.onGuildMessageReceived(event);
        assertThat(shard.send).isEmpty();
    }

    private static class Adder implements Shard.Visitor{
        @Override
        public void visit(@Nonnull Configuration configuration){
            configuration.setPattern(Pattern.compile("pattern"));
        }
    }

    private static class Remover implements Shard.Visitor{
        @Override
        public void visit(@Nonnull Configuration configuration){
            configuration.removePattern();
        }
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
        };
    }
}
