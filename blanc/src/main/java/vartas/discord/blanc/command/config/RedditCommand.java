/*
 * Copyright (C) 2018 u/Zavarov
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
package vartas.discord.blanc.command.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.api.message.InteractiveMessage;
import vartas.discord.bot.command.entity._ast.ASTEntityType;

import java.util.List;

/**
 * This command returns a list of all reddit feeds in the server.
 */
public class RedditCommand extends RedditCommandTOP{
    public RedditCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * Prints all Reddit feeds, if they exist.
     */
    @Override
    public void run(){
        Multimap<String,TextChannel> map = config.getRedditFeeds(guild);
        if(map.isEmpty()){
            communicator.send(channel,"This guild doesn't contain any Reddit feeds.");
        }else {
            InteractiveMessage.Builder builder = new InteractiveMessage.Builder(channel, author, communicator);

            builder.addDescription("All channels and where new submissions from subreddits are posted in:");
            map.asMap().forEach((key, value) -> {
                List<TextChannel> channels = Lists.newArrayList(value);
                for (int i = 0; i < channels.size(); i += 10) {
                    builder.addLine(String.format("`Textchannels for r/%s`", key));
                    for (int j = i; j < Math.min(i + 10, value.size()); ++j) {
                        builder.addLine(channels.get(j).getAsMention());
                    }
                    builder.nextPage();
                }
            });
            communicator.send(builder.build());
        }
    }
}