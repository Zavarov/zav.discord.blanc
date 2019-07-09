/*
 * Copyright (C) 2019 u/Zavarov
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
package vartas.discord.bot.messages;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.atteo.evo.inflector.English;
import vartas.discord.bot.comm.Communicator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * This class creates a Discord message displaying the information of a Discord
 * user.
 */
public abstract class UserMessage {
    /**
     * Never create instances of this class.
     */
    protected UserMessage(){}
    /**
     * The formatter for the dates.
     */
    protected static final DateTimeFormatter DATE = DateTimeFormatter.RFC_1123_DATE_TIME;
    /**
     * Adds the full name of the member to the message.
     * @param builder the message builder.
     * @param user the member in question.
     */
    protected static void addName(EmbedBuilder builder, User user){
        builder.addField("Full Name",String.format("%s#%s",
                user.getName(),
                user.getDiscriminator()),false
        );
    }
    /**
     * Adds the id of the user account to the message.
     * @param builder the message builder.
     * @param user the member in question.
     */
    protected static void addId(EmbedBuilder builder, User user){
        builder.addField("ID",user.getId(),false);
    }
    /**
     * Adds the date when the account was created to the message.
     * @param builder the message builder.
     * @param user the member in question.
     */
    protected static void addCreated(EmbedBuilder builder, User user){
        int days = (int)DAYS.between(user.getCreationTime().toLocalDate(),LocalDate.now());
        builder.addField("Created",String.format("%s (%d %s ago)",
                DATE.format(user.getCreationTime()),
                days,
                English.plural("day", days)),false
        );
        
    }
    /**
     * Adds the avatar of the user to the page, if it isn't the default one.
     * @param builder the message builder.
     * @param user the member in question.
     */
    protected static void addThumbnail(InteractiveMessage.Builder builder, User user){
        String avatar = user.getAvatarUrl();
        if(avatar != null)
            builder.setThumbnail(avatar);
    }
    /**
     * Shows the information about the specified member. If the parameter are
     * empty, the user who executed the command is used.
     * @param author the user who caused this message.
     * @param user the user in question.
     * @param channel the channel the message is in.
     * @param comm the communicator in the shard the message is in.
     * @return an interactive message displaying the members information
     */
    public static InteractiveMessage create(User author, User user, TextChannel channel, Communicator comm){
        InteractiveMessage.Builder builder = new InteractiveMessage.Builder(channel, author, comm);
        addThumbnail(builder,user);
        
        EmbedBuilder embed = new EmbedBuilder();
        embed.addField("Description", String.format("The basic information about %s", user.getAsMention()), false);
        addName(embed,user);
        addId(embed,user);
        addCreated(embed,user);
        builder.addPage(embed);
        return builder.build();
    }
    
}
