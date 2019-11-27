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

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import vartas.discord.bot.entities.DiscordCommunicator;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class BlacklistListener extends ListenerAdapter {
    protected DiscordCommunicator communicator;
    protected Map<Guild, Pattern> blacklist = Maps.newConcurrentMap();

    public BlacklistListener(DiscordCommunicator communicator){
        this.communicator = communicator;
    }

    public void set(Guild guild, Pattern pattern){
        blacklist.put(guild, pattern);
    }

    public void remove(Guild guild){
        blacklist.remove(guild);
    }

    /**
     * Checks if the message contains any blacklisted words.
     * @param event the corresponding event.
     */
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        SelfUser self = event.getJDA().getSelfUser();
        User author = event.getAuthor();
        //Ignore everything this bot posts
        if(self.equals(author))
            return;

        Optional<Pattern> patternOpt = Optional.ofNullable(blacklist.getOrDefault(event.getGuild(),null));
        //Delete the message on a match
        patternOpt.ifPresent(pattern -> {
            if(pattern.matcher(event.getMessage().getContentRaw()).matches())
                communicator.send(event.getMessage().delete());
        });
    }
}
