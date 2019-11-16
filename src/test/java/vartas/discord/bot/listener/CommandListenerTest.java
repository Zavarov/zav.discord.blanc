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

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractBotTest;
import vartas.discord.bot.Command;
import vartas.discord.bot.TestCommandBuilder;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandListenerTest extends AbstractBotTest {
    CommandListener listener;
    MessageReceivedEvent event;
    AtomicBoolean flag;

    @Before
    public void setUp(){
        flag = new AtomicBoolean(false);
        listener = new CommandListener(communicator, new TestCommandBuilder(() -> new Command() {
            @Override
            public void run() {
                flag.set(true);
            }
        }));
        event = new MessageReceivedEvent(jda, 54321L, message);

    }

    @Test
    public void setTest(){
        assertThat(listener.prefixes).isEmpty();
        listener.set(guild, "local");
        assertThat(listener.prefixes).containsEntry(guild, "local");
    }

    @Test
    public void removeTest(){
        assertThat(listener.prefixes).isEmpty();
        listener.set(guild, "local");
        assertThat(listener.prefixes).containsEntry(guild, "local");
        listener.remove(guild);
        assertThat(listener.prefixes).isEmpty();
    }

    @Test
    public void onMessageReceivedTest(){
        listener.onMessageReceived(event);
        assertThat(flag).isTrue();
    }
}
