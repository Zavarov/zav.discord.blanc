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

package vartas.discord.bot;

import net.dv8tion.jda.api.entities.Message;
import org.junit.Before;
import vartas.discord.bot.entities.Command;
import vartas.discord.bot.entities.CommandBuilder;
import vartas.discord.bot.entities.DiscordCommunicator;
import vartas.discord.bot.entities.DiscordEnvironment;

import java.util.function.Function;

public abstract class AbstractBotTest extends AbstractTest {
    protected DiscordEnvironment environment;
    protected DiscordCommunicator communicator;
    protected Function<DiscordCommunicator, CommandBuilder> builder;

    @Before
    public void initBot(){
        builder = (c) -> new CommandBuilder() {
            @Override
            public Command build(String content, Message source) {
                return null;
            }
        };

        environment = new DiscordEnvironment(rank, configuration);
        communicator = new DiscordCommunicator(environment, jda, builder);
    }
}
