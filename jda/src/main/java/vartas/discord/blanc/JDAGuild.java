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
import vartas.discord.blanc.factory.GuildFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;

@Nonnull
public class JDAGuild extends Guild{
    @Nonnull
    private final net.dv8tion.jda.api.entities.Guild guild;

    public JDAGuild(@Nonnull net.dv8tion.jda.api.entities.Guild guild){
        this.guild = guild;
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

    public static Guild create(net.dv8tion.jda.api.entities.Guild guild){
        return GuildFactory.create(() -> new JDAGuild(guild), guild.getIdLong(), guild.getName());
    }
}
