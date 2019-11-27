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

import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import org.junit.Before;
import org.junit.Test;
import vartas.discord.bot.AbstractTest;
import vartas.discord.bot.message.InteractiveMessage;
import vartas.discord.bot.message.builder.InteractiveMessageBuilder;

import static org.assertj.core.api.Assertions.assertThat;

public class InteractiveMessageListenerTest extends AbstractTest {
    InteractiveMessageListener listener;
    MessageReactionAddEvent event;
    InteractiveMessage interactiveMessage;
    MessageEmbed firstPage;
    @Before
    public void setUp(){
        listener = new InteractiveMessageListener(adapter.config());
        event = new MessageReactionAddEvent(jda, 54321L, user, messageReaction);
        interactiveMessage = new InteractiveMessageBuilder(user, communicator).nextPage().nextPage().build();
        firstPage = interactiveMessage.build();

        listener.add(message, interactiveMessage);
    }

    @Test
    public void onMessageReactionAddIsBotTest(){
        user.setBot(true);

        listener.onMessageReactionAdd(event);
        assertThat(interactiveMessage.build()).isEqualTo(firstPage);
    }

    @Test
    public void onMessageReactionAddInMessageChannelTest(){
        messageChannelType = ChannelType.PRIVATE;

        listener.onMessageReactionAdd(event);
        assertThat(interactiveMessage.build()).isNotEqualTo(firstPage);
    }

    @Test
    public void onMessageReactionAddInTextChannelTest(){
        listener.onMessageReactionAdd(event);
        assertThat(interactiveMessage.build()).isNotEqualTo(firstPage);
    }
}
