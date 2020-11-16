/*
 * Copyright (c) 2020 Zavarov
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

package vartas.discord.blanc.command.legacy;

import vartas.discord.blanc.Shard;
import vartas.discord.blanc.TextChannel;

/**
 * This command allows to link subreddits to channels.
 */
public class RedditCommand extends RedditCommandTOP {
    @Override
    public void run(){
        TextChannel textChannel = getTextChannel().orElse(get$TextChannel());
        if(!get$Guild().canInteract(get$Guild().retrieveSelfMember(), textChannel)){
            get$TextChannel().send("I can't interact with "+textChannel.getName());
        }else if(textChannel.containsSubreddits(getSubreddit())){
            textChannel.removeSubreddits(getSubreddit());
            Shard.write(get$Guild(), textChannel);
            get$TextChannel().send("Submissions from r/"+subreddit+" will no longer be posted in "+textChannel+".");
        }else{
            textChannel.addSubreddits(getSubreddit());
            Shard.write(get$Guild(), textChannel);
            get$TextChannel().send("Submissions from r/"+subreddit+" will be posted in "+textChannel.getName()+".");
        }
    }
}
