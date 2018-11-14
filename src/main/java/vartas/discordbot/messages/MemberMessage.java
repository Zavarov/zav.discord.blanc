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
package vartas.discordbot.messages;

import java.awt.Color;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.atteo.evo.inflector.English;
import vartas.discordbot.comm.Communicator;
import vartas.discordbot.messages.InteractiveMessage.Builder;

/**
 * This class creates a Discord message displaying the information of a Discord
 * user.
 * @author u/Zavarov
 */
public final class MemberMessage {
    /**
     * Never create instances of this class.
     */
    private MemberMessage(){}
    /**
     * The formatter for the dates.
     */
    protected static final DateTimeFormatter DATE = DateTimeFormatter.RFC_1123_DATE_TIME;
    /**
     * Sets the description of the current page of the message.
     * @param builder the message builder.
     * @param desc the description.
     */
    private static void addDescription(Builder builder, String desc){
        builder.addDescription(desc);
    }
    /**
     * Adds the full name of the member to the message.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addName(Builder builder, Member member){
        builder.addLine(String.format("%-10s : %s#%s",
                "Full Name",
                member.getUser().getName(),
                member.getUser().getDiscriminator())
        );
    }
    /**
     * Adds the id of the user account to the message.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addId(Builder builder, Member member){
        builder.addLine(String.format("%-10s : %s",
                "ID",
                member.getUser().getId())
        );
    }
    /**
     * Adds the nickname of the member to the message, if he has one.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addNickname(Builder builder, Member member){
        if(member.getNickname() != null){
            builder.addLine(String.format("%-10s : %s",
                    "Nickname",
                    member.getNickname())
            );
        }
        
    }
    /**
     * Adds the date when the account was created to the message.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addCreated(Builder builder, Member member){
        int days = (int)DAYS.between(member.getUser().getCreationTime().toLocalDate(),LocalDate.now());
        builder.addLine(String.format("%-10s : %s (%d %s ago)",
                "Created",
                DATE.format(member.getUser().getCreationTime()),
                days,
                English.plural("day", days))
        );
        
    }
    /**
     * Adds the date of when the user joined to the message.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addJoined(Builder builder, Member member){
        int days = (int)DAYS.between(member.getJoinDate().toLocalDate(),LocalDate.now());
        builder.addLine(String.format("%-10s : %s (%d %s ago)",
                "Joined",
                DATE.format(member.getJoinDate()),
                days,
                English.plural("day", days))
        );
    }
    /**
     * Adds the current color of the member name, if it is not the default one.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addColor(Builder builder, Member member){
        Color c = member.getColor();
        if(c != null){
            builder.addLine(String.format("%-10s : 0x%02X%02X%02X",
                "Color",
                c.getRed(),
                c.getGreen(),
                c.getBlue())
            );
        }
    }
    /**
     * Adds the current game status to the message, if the user has a status message.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addGame(Builder builder, Member member){
        if(member.getGame() != null){
            String type = member.getGame().getType().toString().toLowerCase(Locale.ENGLISH);
            //Transform the game type into an user friendly string
            if(member.getGame().getType()== Game.GameType.DEFAULT){
                type = "Playing";
            }else{
                type = type.substring(0,1).toUpperCase() + type.substring(1);
            }
            builder.addLine(String.format("%-10s : %s",
                    type,
                    member.getGame().getName())
            );
        }
    }
    /**
     * Adds the total number of roles of the member to the message.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addRoleCount(Builder builder, Member member){
        builder.addLine(String.format("%-10s : %d",
                "#Roles",member.getRoles().size())
        );
    }
    /**
     * Adds all roles of the member to the message.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addRoles(Builder builder, Member member){
        member.getRoles()
                .stream()
                .map(role -> String.format("`%s` - %s", role.getId(), role.getName()))
                .forEach(builder::addLine);
    }
    /**
     * Adds the effective permissions of the member in the specific channel to the message.
     * @param builder the message builder.
     * @param member the member in question.
     * @param channel the channel the member is in.
     */
    private static void addPermissions(Builder builder, Member member, TextChannel channel){
        List<Permission> permissions = Permission.getPermissions(
                PermissionUtil.getEffectivePermission(
                        channel,
                        member)
        );
        Collections.sort(permissions,(o1,o2)->o1.name().compareTo(o2.name()));
        permissions.stream()
                .map(p -> p.getName())
                .forEach(builder::addLine);
        
    }
    /**
     * Adds the avatar of the user to the page, if it isn't the default one.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addThumbnail(Builder builder, Member member){
        String avatar = member.getUser().getAvatarUrl();
        if(avatar != null)
            builder.setThumbnail(avatar);
    }
    /**
     * Shows the information about the specified member. If the parameter are
     * empty, the user who executed the command is used.
     * @param author the user who caused this message.
     * @param member the member in question.
     * @param channel the channel the member is in.
     * @param comm the communicator in the shard the message is in.
     * @return an interactive message displaying the members information
     */
    public static InteractiveMessage create(User author, Member member, TextChannel channel, Communicator comm){
        InteractiveMessage.Builder builder = new InteractiveMessage.Builder(channel, author, comm);
        addThumbnail(builder,member);
        
        addDescription(builder,String.format("The basic information about %s", member.getAsMention()));
        builder.addLine("```");
        addName(builder,member);
        addId(builder,member);
        addNickname(builder,member);
        addCreated(builder,member);
        addJoined(builder,member);
        addColor(builder,member);
        addGame(builder,member);
        addRoleCount(builder,member);
        builder.addLine("```");
        builder.nextPage();
        
        addDescription(builder, "All assigned roles");
        addRoles(builder,member);
        builder.nextPage();
        
        addDescription(builder, String.format("All of %s permissions in %s",member.getAsMention(), channel.getAsMention()));
        addPermissions(builder, member, channel);
        return builder.build();
    }
}
