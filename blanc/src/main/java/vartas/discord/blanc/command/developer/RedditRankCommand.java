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
package vartas.discord.blanc.command.developer;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.command.entity._ast.ASTEntityType;

import java.util.List;

/**
 * This command assigns and removes the Reddit rank.
 */
public class RedditRankCommand extends RedditRankCommandTOP{
    public RedditRankCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * Removes or adds the Rank, depending on the user already having the rank.
     */
    @Override
    public void run(){
        StringBuilder builder = new StringBuilder();
        User user = userSymbol.resolve(source).get();
        if(environment.rank().hasRedditRank(user)){
            environment.rank().removeRedditRank(user);
            builder.append("Removed Reddit rank from ").append(user.getName()).append(".");
        }else{
            environment.rank().addRedditRank(user);
            builder.append("Added Reddit rank to ").append(user.getName()).append(".");
        }
        communicator.send(channel, builder.toString());
    }
}