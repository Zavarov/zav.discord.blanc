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

package vartas.discord.blanc;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import net.dv8tion.jda.api.OnlineStatus;
import org.atteo.evo.inflector.English;
import org.json.JSONArray;
import org.json.JSONObject;
import vartas.discord.blanc.factory.GuildFactory;
import vartas.discord.blanc.json.JSONGuild;
import vartas.discord.blanc.json.JSONRole;
import vartas.discord.blanc.json.JSONTextChannel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Nonnull
public class JDAGuild extends Guild{
    /**
     * A moderator is every user that has as least one of the listed permissions.
     */
    protected static final Set<net.dv8tion.jda.api.Permission> MODERATOR_PERMISSIONS = Sets.newHashSet(
            net.dv8tion.jda.api.Permission.BAN_MEMBERS,
            net.dv8tion.jda.api.Permission.KICK_MEMBERS,
            net.dv8tion.jda.api.Permission.MANAGE_CHANNEL,
            net.dv8tion.jda.api.Permission.MANAGE_PERMISSIONS,
            net.dv8tion.jda.api.Permission.MANAGE_ROLES,
            net.dv8tion.jda.api.Permission.MANAGE_SERVER
    );
    /**
     * An administrator is every user that has all of the listed permissions.
     */
    protected static final Set<net.dv8tion.jda.api.Permission> ADMINISTRATOR_PERMISSIONS = Sets.newHashSet(
            net.dv8tion.jda.api.Permission.ADMINISTRATOR
    );
    /**
     * The date pretty printer.
     */
    protected static final DateTimeFormatter DATE = DateTimeFormatter.RFC_1123_DATE_TIME;
    @Nonnull
    private final net.dv8tion.jda.api.entities.Guild guild;

    public JDAGuild(@Nonnull net.dv8tion.jda.api.entities.Guild guild){
        this.guild = guild;
    }

    @Override
    public void leave(){
        guild.leave().complete();
    }

    @Override
    @Nonnull
    public TextChannel getChannels(@Nonnull Long key){
        try{
            return getChannels(key, () -> {
                net.dv8tion.jda.api.entities.TextChannel textChannel = guild.getTextChannelById(key);
                Preconditions.checkNotNull(textChannel, TypeResolverException.of(Errors.UNKNOWN_ENTITY));
                return JDATextChannel.create(textChannel);
            });
        }catch(ExecutionException e){
            //TODO Internal error
            throw new RuntimeException("Internal error: " + e.getMessage());
        }
    }

    @Override
    @Nonnull
    public Role getRoles(@Nonnull Long key){
        try{
            return getRoles(key, () -> {
                net.dv8tion.jda.api.entities.Role role = guild.getRoleById(key);
                Preconditions.checkNotNull(role, TypeResolverException.of(Errors.UNKNOWN_ENTITY));
                return JDARole.create(role);
            });
        }catch(ExecutionException e){
            //TODO Internal error
            throw new RuntimeException("Internal error: " + e.getMessage());
        }
    }

    @Override
    @Nonnull
    public Member getMembers(@Nonnull Long key){
        try{
            return getMembers(key, () -> {
                net.dv8tion.jda.api.entities.Member member = guild.getMemberById(key);
                Preconditions.checkNotNull(member, TypeResolverException.of(Errors.UNKNOWN_ENTITY));
                return JDAMember.create(member);
            });
        }catch(ExecutionException e){
            //TODO Internal error
            throw new RuntimeException("Internal error: " + e.getMessage());
        }
    }

    @Override
    public boolean canInteract(Member member, TextChannel textChannel){
        net.dv8tion.jda.api.entities.Member jdaMember = guild.getMemberById(member.getId());
        net.dv8tion.jda.api.entities.TextChannel jdaTextChannel = guild.getTextChannelById(textChannel.getId());

        if(jdaMember == null || jdaTextChannel == null)
            return false;
        else
            return jdaTextChannel.canTalk(jdaMember);
    }

    @Override
    public boolean canInteract(Member member, Role role){
        net.dv8tion.jda.api.entities.Member jdaMember = guild.getMemberById(member.getId());
        net.dv8tion.jda.api.entities.Role jdaRole = guild.getRoleById(role.getId());

        if(jdaMember == null || jdaRole == null)
            return false;
        else
            return jdaMember.canInteract(jdaRole);
    }

    public static Guild create(@Nonnull net.dv8tion.jda.api.entities.Guild jdaGuild, @Nullable JSONObject jsonObject){
        //Optional<String> prefix,  Cache<Long,TextChannel> channels,  Cache<Long,Role> roles,  List<String> blacklist
        Guild guild = GuildFactory.create(
                () -> new JDAGuild(jdaGuild),
                JDASelfMember.create(jdaGuild.getSelfMember()),
                jdaGuild.getIdLong(),
                jdaGuild.getName()
        );

        if(jsonObject != null){
            guild.setPrefix(Optional.ofNullable(jsonObject.optString(JSONGuild.PREFIX)));

            //Load channels
            JSONArray jsonChannels = jsonObject.getJSONArray(JSONGuild.CHANNELS);
            for(int i = 0 ; i < jsonChannels.length() ; ++i) {
                JSONObject jsonTextChannel = jsonChannels.getJSONObject(i);
                long textChannelId = jsonTextChannel.getLong(JSONTextChannel.ID);
                net.dv8tion.jda.api.entities.TextChannel jdaTextChannel = jdaGuild.getTextChannelById(textChannelId);

                //Channel might've been removed since the last initialization
                if(jdaTextChannel != null)
                    guild.putChannels(textChannelId, JDATextChannel.create(jdaTextChannel, jsonTextChannel));
            }

            //Load roles
            JSONArray jsonRoles = jsonObject.getJSONArray(JSONGuild.ROLES);
            for(int i = 0 ; i < jsonRoles.length() ; ++i){
                JSONObject jsonRole = jsonRoles.getJSONObject(i);
                long roleId = jsonRole.getLong(JSONRole.ID);
                net.dv8tion.jda.api.entities.Role jdaRole = jdaGuild.getRoleById(roleId);

                //Channel might've been removed since the last initialization
                if(jdaRole != null)
                    guild.putRoles(roleId, JDARole.create(jdaRole, jsonRole));
            }

            //Load blacklist
            JSONArray jsonBlacklist = jsonObject.getJSONArray(JSONGuild.BLACKLIST);
            for(int i = 0 ; i < jsonBlacklist.length() ; ++i)
                guild.addBlacklist(jsonBlacklist.getString(i));

            //Derive blacklist pattern
            guild.compilePattern();
        }

        return guild;
    }

    public static Guild create(@Nonnull net.dv8tion.jda.api.entities.Guild jdaGuild){
        return create(jdaGuild, null);
    }

    //------------------------------------------------------------------------------------------------------------------
    //
    //      Printable
    //
    //------------------------------------------------------------------------------------------------------------------

    @Nonnull
    @Override
    public MessageEmbed toMessageEmbed(){
        MessageEmbed messageEmbed = new MessageEmbed();

        setTitle(messageEmbed);
        setThumbnail(messageEmbed);
        addOwner(messageEmbed);
        addRegion(messageEmbed);
        addTextChannels(messageEmbed);
        addVoiceChannels(messageEmbed);
        addAdministrators(messageEmbed);
        addModerators(messageEmbed);
        addMembers(messageEmbed);
        addRoles(messageEmbed);
        addCreated(messageEmbed);

        return messageEmbed;
    }

    private void setTitle(MessageEmbed messageEmbed){
        messageEmbed.setTitle(guild.getName());
    }

    private void setThumbnail(MessageEmbed messageEmbed){
        //Icon may be null if the guild uses the default icon
        if(guild.getIconUrl() != null)
            messageEmbed.setThumbnail(guild.getIconUrl());
    }

    private void addOwner(MessageEmbed messageEmbed){
        //Owner may be null if the account was suspended
        if(guild.getOwner() != null)
            messageEmbed.addFields("Owner",guild.getOwner().getUser().getAsMention(), true);
    }

    private void addRegion(MessageEmbed messageEmbed){
        messageEmbed.addFields("Region", guild.getRegion().getName(), true);
    }

    private void addTextChannels(MessageEmbed messageEmbed){
        int size = guild.getTextChannels().size();
        messageEmbed.addFields(English.plural("#TextChannel",size), size, true);
    }

    private void addVoiceChannels(MessageEmbed messageEmbed){
        int size = guild.getVoiceChannels().size();
        messageEmbed.addFields(English.plural("#VoiceChannel",size), size, true);
    }

    private void addAdministrators(MessageEmbed messageEmbed){
        //The owner is always an administrator, however, that account may be suspended
        List<net.dv8tion.jda.api.entities.Member> administrators = guild.getMembers()
                .stream()
                .filter(m -> !m.getUser().isBot())
                .filter(m -> m.getPermissions().containsAll(ADMINISTRATOR_PERMISSIONS))
                .sorted(Comparator.comparingLong(u -> u.getUser().getIdLong()))
                .collect(Collectors.toList());

        administrators.stream()
                .map(net.dv8tion.jda.api.entities.Member::getAsMention)
                .reduce((u, v) -> u + "\n" + v)
                .ifPresent(content -> messageEmbed.addFields(English.plural("Admin", administrators.size()), content));

    }

    private void addModerators(MessageEmbed messageEmbed){
        List<net.dv8tion.jda.api.entities.Member> moderators = guild.getMembers().stream()
                .filter(m -> !m.getUser().isBot())
                .filter(m -> !m.getPermissions().containsAll(ADMINISTRATOR_PERMISSIONS))
                .filter(m -> !Collections.disjoint(m.getPermissions(), MODERATOR_PERMISSIONS))
                .collect(Collectors.toList());

        moderators.stream()
                .map(net.dv8tion.jda.api.entities.Member::getAsMention)
                .reduce((u, v) -> u + "\n" + v)
                .ifPresent(content -> messageEmbed.addFields(English.plural("Moderator", moderators.size()), content));
    }

    private void addMembers(MessageEmbed messageEmbed){
        int online = (int)guild.getMembers().stream()
                .filter(e -> !e.getUser().isBot())
                .filter(e -> !e.getOnlineStatus().equals(OnlineStatus.OFFLINE))
                .count();

        int total = (int)guild.getMembers().stream()
                .filter(e -> !e.getUser().isBot())
                .count();

        messageEmbed.addFields(English.plural("#Member",total), String.format("%d / %d",online,total), true);

    }

    private void addRoles(MessageEmbed messageEmbed){
        int size = guild.getRoles().size();
        messageEmbed.addFields(English.plural("#Role",size), size, true);
    }

    private void addCreated(MessageEmbed messageEmbed){
        messageEmbed.addFields("Created", DATE.format(guild.getTimeCreated()), true);
    }
}
