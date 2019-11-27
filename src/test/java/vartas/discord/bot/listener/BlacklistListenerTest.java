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

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.internal.entities.SelfUserImpl;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

public class BlacklistListenerTest extends AbstractTest {
    BlacklistListener listener;
    GuildMessageReceivedEvent event;
    @Before
    public void setUp(){
        listener = new BlacklistListener(communicator);
        listener.set(guild, Pattern.compile("invalid"));
        event = new GuildMessageReceivedEvent(jda, 54321L, message);
        jda.setSelfUser(new SelfUserImpl(50, jda));
    }

    @Test
    public void onGuildMessageReceivedFromItselfTest(){
        jda.setSelfUser(user);

        assertThat(communicator.send).isEmpty();
        listener.onGuildMessageReceived(event);
        assertThat(communicator.send).isEmpty();
    }

    @Test
    public void onGuildMessageReceivedMatchTest(){
        messageContent = "invalid";

        assertThat(communicator.send).isEmpty();
        listener.onGuildMessageReceived(event);
        assertThat(communicator.send).hasSize(1);
    }

    @Test
    public void onGuildMessageReceivedNoMatchTest(){
        messageContent = "valid";

        assertThat(communicator.send).isEmpty();
        listener.onGuildMessageReceived(event);
        assertThat(communicator.send).isEmpty();
    }

    @Test
    public void onGuildMessageReceivedNoPatternTest(){
        listener.remove(guild);
        messageContent = "invalid";

        assertThat(communicator.send).isEmpty();
        listener.onGuildMessageReceived(event);
        assertThat(communicator.send).isEmpty();
    }
}
