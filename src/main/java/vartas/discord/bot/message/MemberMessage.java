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

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.internal.utils.PermissionUtil;
import org.apache.commons.lang3.StringUtils;
import org.atteo.evo.inflector.English;
import vartas.discord.bot.entities.Shard;
import vartas.discord.bot.message.builder.InteractiveMessageBuilder;

import java.awt.*;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import static java.time.temporal.ChronoUnit.DAYS;

/**
 * This class creates a Discord message displaying the information of a Discord
 * user which is also part of a guild.
 */
public abstract class MemberMessage extends UserMessage{
    /**
     * Never create instances of this class.
     */
    protected MemberMessage(){}
    /**
     * Sets the description of the current page of the message.
     * @param builder the message builder.
     * @param desc the description.
     */
    private static void addDescription(InteractiveMessageBuilder builder, String desc){
        builder.addDescription(desc);
    }
    /**
     * Adds the nickname of the member to the message, if he has one.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addNickname(EmbedBuilder builder, Member member){
        if(member.getNickname() != null){
            builder.addField("Nickname",member.getNickname(),true);
        }
        
    }
    /**
     * Adds the date of when the user joined to the message.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addJoined(EmbedBuilder builder, Member member){
        int days = (int)DAYS.between(member.getTimeJoined().toLocalDate(),LocalDate.now());
        builder.addField("Joined",String.format("%s\n(%d %s ago)",
                DATE.format(Date.from(member.getTimeJoined().toInstant())),
                days,
                English.plural("day", days)),true
        );
    }
    /**
     * Adds the current color of the member name, if it is not the default one.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addColor(EmbedBuilder builder, Member member){
        Color c = member.getColor();
        if(c != null){
            builder.addField("Color",String.format("0x%02X%02X%02X",
                c.getRed(),
                c.getGreen(),
                c.getBlue()),
                true
            );
        }
    }
    /**
     * Adds the current game status to the message, if the user has a status message.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addActivity(EmbedBuilder builder, Member member){
        for(Activity activity : member.getActivities()){
            String type;
            //Transform the game type into an user friendly string
            if(activity.getType()== Activity.ActivityType.DEFAULT){
                type = "Playing";
            }else{
                type = activity.getType().name().toLowerCase(Locale.ENGLISH);
                type = StringUtils.capitalize(type);
            }
            builder.addField(type, activity.getName(),false);
        }
    }
    /**
     * Adds the total number of roles of the member to the message.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addRoleCount(EmbedBuilder builder, Member member){
        builder.addField("#Roles",Integer.toString(member.getRoles().size()),true);
    }
    /**
     * Adds all roles of the member to the message.
     * @param builder the message builder.
     * @param member the member in question.
     */
    private static void addRoles(InteractiveMessageBuilder builder, Member member){
        member.getRoles()
                .stream()
                .map(role -> String.format("`%s` - %s", role.getId(), role.getName()))
                .forEach(builder::addLine);
    }
    /**
     * Adds the effective ranks of the member in the specific channel to the message.
     * @param builder the message builder.
     * @param member the member in question.
     * @param channel the channel the member is in.
     */
    private static void addPermissions(InteractiveMessageBuilder builder, Member member, TextChannel channel){
        Permission.getPermissions(PermissionUtil.getEffectivePermission(channel,member))
                .stream()
                .sorted(Comparator.comparing(Enum::name))
                .map(Permission::getName)
                .forEach(builder::addLine);
        
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
    public static InteractiveMessage create(User author, Member member, TextChannel channel, Shard comm){
        InteractiveMessageBuilder builder = new InteractiveMessageBuilder(author, comm);
        addThumbnail(builder,member.getUser());

        EmbedBuilder embed = new EmbedBuilder();
        addName(embed,member.getUser());
        addNickname(embed,member);
        addId(embed,member.getUser());
        addColor(embed,member);
        addRoleCount(embed,member);
        addCreated(embed,member.getUser());
        addJoined(embed,member);
        addActivity(embed,member);
        builder.addPage(embed);
        
        addDescription(builder, "All assigned roles");
        addRoles(builder,member);
        builder.nextPage();
        
        addDescription(builder, String.format("All of %s ranks in %s",member.getAsMention(), channel.getAsMention()));
        addPermissions(builder, member, channel);
        return builder.build();
    }
}
