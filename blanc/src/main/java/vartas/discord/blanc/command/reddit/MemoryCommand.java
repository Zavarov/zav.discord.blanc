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
package vartas.discord.blanc.command.reddit;

import net.dv8tion.jda.core.entities.Message;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.api.environment.RedditInterface;
import vartas.discord.bot.api.message.InteractiveMessage;
import vartas.discord.bot.command.entity._ast.ASTEntityType;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This command shows all dates from which data has been requested.
 */
public class MemoryCommand extends MemoryCommandTOP{
    public MemoryCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * Creates an interactive message with all dates.
     */
    @Override
    public void run(){
        Instant from = fromSymbol.resolve().get().toInstant();
        Instant to = toSymbol.resolve().get().toInstant();
        String subreddit = subredditSymbol.resolve();

        SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d, ''yy");
        InteractiveMessage.Builder builder = new InteractiveMessage.Builder(channel, author, communicator);

        List<String> dates = RedditInterface.listRequestedDates(subreddit, from, to)
                .stream()
                .map(Date::from)
                .map(formatter::format)
                .collect(Collectors.toList());
        
        if(dates.isEmpty()){
            communicator.send(channel, "No data in the given interval has been requested so far.");
        }else{
            builder.addDescription("All stored data over the given interval.");
            builder.addLines(dates, 20);
            communicator.send(builder.build());
        }
    }
}