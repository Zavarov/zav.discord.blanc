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

import net.dv8tion.jda.api.requests.RestAction;
import vartas.discord.bot.entities.DiscordCommunicator;
import vartas.discord.bot.entities.DiscordEnvironment;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class AbstractBotTest extends AbstractTest {
    protected static Function<DiscordCommunicator, CommandBuilder> builder;
    protected static EntityAdapter adapter;
    protected static DiscordCommunicator communicator;
    protected static DiscordEnvironment environment;

    static {
        try {
            Path config = Paths.get("src/test/resources/config.json");
            Path status = Paths.get("src/test/resources/status.json");
            Path rank = Paths.get("src/test/resources/rank.json");
            Path guilds = Paths.get("src/test/guilds");
            adapter = new JSONEntityAdapter(config, status, rank, guilds);

            builder = (c) -> new TestCommandBuilder(() -> new Command() {
                @Override
                public void run() {

                }
            });

            environment = new DiscordEnvironment(adapter, builder);
            communicator = new DiscordCommunicator(environment, jda, builder, adapter){
                @Override
                public <T> void send(RestAction<T> action, Consumer<T> success, Consumer<Throwable> failure){
                }
            };

            environment.shutdown();
            communicator.shutdown();
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
