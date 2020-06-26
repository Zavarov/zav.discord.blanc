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

import vartas.discord.blanc.io.json.JSONRanks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class JDASelfMember extends SelfMember{
    @Nonnull
    private final net.dv8tion.jda.api.entities.Member member;

    public JDASelfMember(@Nonnull net.dv8tion.jda.api.entities.Member member){
        this.member = member;
    }

    @Nonnull
    public static SelfMember create(net.dv8tion.jda.api.entities.Member member){
        JDASelfMember selfMember = new JDASelfMember(member);

        selfMember.setRanks(JSONRanks.RANKS.getRanks().get(member.getIdLong()));
        selfMember.setId(member.getIdLong());
        selfMember.setName(member.getUser().getName());

        return selfMember;
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
    public void modifyNickname(@Nullable String nickname){
        member.modifyNickname(nickname).complete();
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
}
