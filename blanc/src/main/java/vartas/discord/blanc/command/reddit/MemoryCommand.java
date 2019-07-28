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

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
        Instant from = super.from.toInstant();
        Instant to = super.to.toInstant();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM ''yy", Locale.ENGLISH);
        InteractiveMessage.Builder builder = new InteractiveMessage.Builder(channel, author, communicator);

        //Group all stored files by the month
        Map<YearMonth, List<Instant>> dates = RedditInterface
                .listRequestedDates(subreddit, from, to)
                .stream()
                .collect(Collectors.groupingBy(this::getStartOfMonth));
        
        if(dates.isEmpty()){
            communicator.send(channel, "No data in the given interval has been requested so far.");
        }else{
            //Print all files of a month on one page
            for(Map.Entry<YearMonth, List<Instant>> entry : dates.entrySet()){
                YearMonth month = entry.getKey();

                builder.addDescription(entry.getKey().format(formatter));

                for(int week = 0 ; week < month.lengthOfMonth() ; week += 7){

                    StringBuilder line = new StringBuilder();
                    line.append("`");
                    for(int day = week ; day < Math.min(month.lengthOfMonth(), week+7) ; day++){
                        //Add a space between each day
                        if(day > week)
                            line.append(" ");
                        //All days have two characters
                        line.append(String.format("%02d", day+1));

                    }
                    line.append("`");
                    builder.addLine(line.toString());
                }

                builder.nextPage();
            }
        }
        communicator.send(builder.build());
    }

    private YearMonth getStartOfMonth(Instant date){
        return YearMonth.from(date.atZone(ZoneId.of("UTC")).toLocalDate());
    }
}