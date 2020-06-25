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
package vartas.discord.message;

import com.google.common.collect.Sets;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.atteo.evo.inflector.English;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class creates a Discord message displaying the information of a Discord
 * guild.
 */
public abstract class ServerMessage {
    /**
     * Never create instances of this class.
     */
    protected ServerMessage(){}
    /**
     * A moderator is every user that has as least one of the ranks listed here.
     */
    protected static final Set<Permission> MODERATOR = Sets.newHashSet(
            Permission.BAN_MEMBERS,
            Permission.KICK_MEMBERS,
            Permission.MANAGE_CHANNEL,
            Permission.MANAGE_PERMISSIONS,
            Permission.MANAGE_ROLES,
            Permission.MANAGE_SERVER
    );
    /**
     * The formatter for the dates.
     */
    protected static final DateTimeFormatter DATE = DateTimeFormatter.RFC_1123_DATE_TIME;
    /**
     * Adds the title of the guild to the message.
     * @param builder the message builder.
     * @param guild the guild in question.
     */
    private static void addDescription(EmbedBuilder builder, Guild guild){
        builder.setDescription(guild.getName());
    }
    /**
     * Adds the icon of the guild as a thumbnail, if it has one.
     * @param builder the message builder.
     * @param guild the guild in question.
     */
    private static void addThumbnail(EmbedBuilder builder, Guild guild){
        if(guild.getIconUrl() != null)
            builder.setThumbnail(guild.getIconUrl());
    }
    /**
     * Adds the owner of the guild to the message.
     * @param builder the message builder.
     * @param guild the guild in question.
     */
    private static void addOwner(EmbedBuilder builder, Guild guild){
        builder.addField("Owner",guild.getOwner().getUser().getAsMention(),true);
    }
    /**
     * Adds the region the guild is in to the message.
     * @param builder the message builder.
     * @param guild the guild in question.
     */
    private static void addRegion(EmbedBuilder builder, Guild guild){
        builder.addField("Region",guild.getRegion().getName(),true);
    }
    /**
     * Adds the number of text channels to the message.
     * @param builder the message builder.
     * @param guild the guild in question.
     */
    private static void addTextChannels(EmbedBuilder builder, Guild guild){
        int size = guild.getTextChannels().size();
        builder.addField(English.plural("#TextChannel",size),Integer.toString(size),true);
    }
    /**
     * Adds the number of voice channels to the message.
     * @param builder the message builder.
     * @param guild the guild in question.
     */
    private static void addVoiceChannels(EmbedBuilder builder, Guild guild){
        int size = guild.getVoiceChannels().size();
        builder.addField(English.plural("#VoiceChannel",size),Integer.toString(size),true);
    }
    /**
     * Adds the number of admins to the message.
     * Note that the owner is always an admin, thus this will never be empty.
     * @param builder the message builder.
     * @param guild the guild in question.
     */
    private static void addAdmins(EmbedBuilder builder, Guild guild){
        //The owner always has admin rights -> not empty
        List<Member> admins = guild.getMembers()
                .stream()
                .filter(m -> !m.getUser().isBot())
                .filter(m -> m.getPermissions().contains(Permission.ADMINISTRATOR))
                .sorted(Comparator.comparingLong(u -> u.getUser().getIdLong()))
                .collect(Collectors.toList());
        String text = admins.stream().map(Member::getAsMention).reduce((u,v) -> u + "\n" + v).get();
        
        builder.addField(English.plural("Admin",admins.size()), text, false);
    }
    /**
     * Adds the moderators to the list that aren't admins. Otherwise it won't
     * be added.
     * @param builder the message builder.
     * @param guild the guild in question.
     */
    private static void addModerators(EmbedBuilder builder, Guild guild){
        List<Member> moderators = guild.getMembers().stream()
                .filter(m -> !m.getUser().isBot())
                .filter(m -> !m.getPermissions().contains(Permission.ADMINISTRATOR))
                .filter(m -> !Collections.disjoint(m.getPermissions(), MODERATOR))
                .collect(Collectors.toList());
        //The owner is always an admin
        if(!moderators.isEmpty()){
            String text = moderators.stream().map(Member::getAsMention).reduce((u,v) -> u + "\n" + v).get();
            builder.addField(English.plural("Moderator", moderators.size()),text,false);
        }
    }
    /**
     * Adds the number of members and the number of the ones that are currently
     * online to the message.
     * @param builder the message builder.
     * @param guild the guild in question.
     */
    private static void addMembers(EmbedBuilder builder, Guild guild){
        int online = (int)guild.getMembers().stream()
                .filter(e -> !e.getUser().isBot())
                .filter(e -> !e.getOnlineStatus().equals(OnlineStatus.OFFLINE))
                .count();
        int total = (int)guild.getMembers().stream()
                .filter(e -> !e.getUser().isBot())
                .count();
        builder.addField(English.plural("#Member",total),String.format("%d / %d",online,total),true);
        
    }
    /**
     * Adds the number of roles to the message.
     * @param builder the message builder.
     * @param guild the guild in question.
     */
    private static void addRoles(EmbedBuilder builder, Guild guild){
        int size = guild.getRoles().size();
        builder.addField(English.plural("#Role",size),Integer.toString(size),true);
    }
    /**
     * Adds the date when the guild was created to the message.
     * @param builder the message builder.
     * @param guild the guild in question.
     */
    private static void addCreated(EmbedBuilder builder, Guild guild){
        builder.addField("Created",DATE.format(guild.getTimeCreated()),true);
    }
    /**
     * Shows the information about the specified guild.
     * @param guild the guild in question.
     * @return an interactive message displaying the guilds information
     */
    public static MessageEmbed create(Guild guild){
        EmbedBuilder builder = new EmbedBuilder();
        
        addDescription(builder, guild);
        addThumbnail(builder, guild);
        addOwner(builder, guild);
        addRegion(builder, guild);
        addTextChannels(builder, guild);
        addVoiceChannels(builder, guild);
        addAdmins(builder, guild);
        addModerators(builder, guild);
        addMembers(builder, guild);
        addRoles(builder, guild);
        addCreated(builder, guild);
        return builder.build();
    }
}
