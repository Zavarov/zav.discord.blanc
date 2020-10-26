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

import net.dv8tion.jda.api.entities.Activity;
import org.apache.commons.lang3.StringUtils;
import org.atteo.evo.inflector.English;
import vartas.discord.blanc.factory.MemberFactory;
import vartas.discord.blanc.factory.MessageEmbedFactory;
import vartas.discord.blanc.io.json.JSONRanks;

import javax.annotation.Nonnull;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.DAYS;

public class JDAMember extends Member{
    /**
     * The formatter for the dates.
     */
    private static final SimpleDateFormat DATE = new SimpleDateFormat("EEE, d MMM ''yy z", Locale.ENGLISH);

    static{
        DATE.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    @Nonnull
    private final net.dv8tion.jda.api.entities.Member member;

    public JDAMember(@Nonnull net.dv8tion.jda.api.entities.Member member){
        this.member = member;
    }

    @Nonnull
    public static Member create(net.dv8tion.jda.api.entities.Member member){
        return MemberFactory.create(
                () -> new JDAMember(member),
                Optional.empty(),                           //Private Channel
                JSONRanks.RANKS.getRanks().get(member.getIdLong()),
                member.getIdLong(),
                member.getUser().getName()
        );
    }

    @Nonnull
    @Override
    public List<Permission> getPermissions(@Nonnull TextChannel textChannel) {
        net.dv8tion.jda.api.entities.TextChannel jdaTextChannel = member.getGuild().getTextChannelById(textChannel.getId());

        if(jdaTextChannel == null)
            return Collections.emptyList();
        else
            return member.getPermissions(jdaTextChannel).stream().map(JDAPermission::transform).collect(Collectors.toList());
    }

    @Override
    public Optional<PrivateChannel> getChannel(){
        return Optional.of(JDAPrivateChannel.create(member.getUser().openPrivateChannel().complete()));
    }

    @Override
    public List<Role> retrieveRoles(){
        return member.getRoles().stream().map(JDARole::create).collect(Collectors.toList());
    }

    @Override
    public void modifyRoles(Collection<Role> rolesToAdd, Collection<Role> rolesToRemove) {
        List<net.dv8tion.jda.api.entities.Role> jdaRolesToAdd = rolesToAdd.stream()
                .map(Snowflake::getId)
                .map(member.getGuild()::getRoleById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<net.dv8tion.jda.api.entities.Role> jdaRolesToRemove = rolesToRemove.stream()
                .map(Snowflake::getId)
                .map(member.getGuild()::getRoleById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        member.getGuild().modifyMemberRoles(member, jdaRolesToAdd, jdaRolesToRemove).complete();
    }

    @Override
    public Optional<String> retrieveNickname(){
        return Optional.ofNullable(member.getNickname());
    }

    @Override
    public String getAsMention(){
        return member.getAsMention();
    }

    //------------------------------------------------------------------------------------------------------------------
    //
    //      Printable
    //
    //------------------------------------------------------------------------------------------------------------------


    @Override
    public MessageEmbed toMessageEmbed(){
        MessageEmbed messageEmbed = MessageEmbedFactory.create();

        setTitle(messageEmbed);
        setThumbnail(messageEmbed);

        addNickname(messageEmbed);
        addId(messageEmbed);
        addColor(messageEmbed);
        addRoleCount(messageEmbed);
        addCreated(messageEmbed);
        addJoined(messageEmbed);
        addActivity(messageEmbed);

        return messageEmbed;
    }

    private void setTitle(MessageEmbed messageEmbed){
        messageEmbed.setTitle(member.getUser().getName());
    }

    private void setThumbnail(MessageEmbed messageEmbed){
        messageEmbed.setThumbnail(member.getUser().getEffectiveAvatarUrl());
    }

    private void addNickname(MessageEmbed messageEmbed){
        if(member.getNickname() != null)
            messageEmbed.addFields("Nickname",member.getNickname(), true);

    }

    protected void addId(MessageEmbed messageEmbed){
        messageEmbed.addFields("ID", member.getId());
    }

    protected void addCreated(MessageEmbed messageEmbed){
        int days = (int)DAYS.between(member.getUser().getTimeCreated().toLocalDate(),LocalDate.now());
        messageEmbed.addFields(
                "Created",
                String.format("%s\n(%d %s ago)",
                    DATE.format(Date.from(member.getUser().getTimeCreated().toInstant())),
                    days,
                    English.plural("day", days)
                ),
                true
        );

    }

    private void addJoined(MessageEmbed messageEmbed){
        int days = (int)DAYS.between(member.getTimeJoined().toLocalDate(), LocalDate.now());
        messageEmbed.addFields(
                "Joined",
                String.format("%s\n(%d %s ago)",
                    DATE.format(Date.from(member.getTimeJoined().toInstant())),
                    days,
                    English.plural("day", days)
                ),
                true
        );
    }

    private void addColor(MessageEmbed messageEmbed){
        Color color = member.getColor();
        if(color != null){
            messageEmbed.addFields(
                    "Color",
                    String.format("0x%02X%02X%02X",
                        color.getRed(),
                        color.getGreen(),
                        color.getBlue()
                    ),
                    true
            );
        }
    }

    private void addActivity(MessageEmbed messageEmbed){
        for(Activity activity : member.getActivities()){
            String type;
            //Transform the game type into an user friendly string
            if(activity.getType()== Activity.ActivityType.DEFAULT) {
                type = "Playing";
            }else if(activity.getType() == Activity.ActivityType.CUSTOM_STATUS){
                type = "Custom Status";
            }else{
                type = activity.getType().name().toLowerCase(Locale.ENGLISH);
                type = StringUtils.capitalize(type);
            }
            messageEmbed.addFields(type, activity.getName());
        }
    }

    private void addRoleCount(MessageEmbed messageEmbed){
        messageEmbed.addFields("#Roles", member.getRoles().size(), true);
    }
}
