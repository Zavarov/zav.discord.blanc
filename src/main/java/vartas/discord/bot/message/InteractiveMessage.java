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
package vartas.discord.bot.message;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import vartas.discord.bot.entities.Shard;

import java.util.List;

/**
 * This class creates an interactive message where the user has the option to
 * move between different "pages".
 */
public class InteractiveMessage {
    /**
     * &lt;-.
     */
    public static final String ARROW_LEFT = "\u2b05";

    /**
     * -&gt;.
     */
    public static final String ARROW_RIGHT = "\u27a1";
    /**
     * All possible pages.
     */
    protected final List<MessageEmbed> pages;
    /**
     * The user that caused this message to be sent.
     */
    protected final User author;
    protected final Shard shard;
    /**
     * The page that is displayed.
     */
    protected int current_page = 0;
    /**
     * creates an message with the specified pages.
     * @param author the author of the message.
     * @param pages the different pages of the message.
     * @param shard the communicator for the local shard.
     */
    public InteractiveMessage(User author, List<MessageEmbed> pages, Shard shard){
        this.author = author;
        this.pages = pages;
        this.shard = shard;
    }

    public void update(User user, MessageChannel channel, String message, String reaction){
        if(!author.equals(user))
            return;

        //Flip the pages on <- and ->
        if(reaction.equals(ARROW_LEFT))
            current_page = (current_page-1+pages.size()) % pages.size();
        else if(reaction.equals(ARROW_RIGHT))
            current_page = (current_page+1) % pages.size();

        //Send the new page
        shard.queue(channel.editMessageById(message, pages.get(current_page)));
    }

    public void update(User user, TextChannel channel, String message, String reaction){
        if(!author.equals(user))
            return;

        update(user, (MessageChannel)channel, message, reaction);

        //Remove the reaction so that the user doesn't have to do it
        if(canRemoveReactions(channel))
            channel.removeReactionById(message, reaction, user).queue();
    }
    /**
     * @return true when the bot has the "Manage Messages" rank.
     */
    private boolean canRemoveReactions(TextChannel channel){
        return PermissionUtil.checkPermission(channel, channel.getGuild().getSelfMember(), Permission.MESSAGE_MANAGE);
    }

    public MessageEmbed build(){
        return pages.get(current_page);
    }
}