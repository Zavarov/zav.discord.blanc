package vartas.discord.bot.api.communicator;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;

import java.util.List;
import java.util.Optional;

/*
 * Copyright (C) 2019 Zavarov
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
public interface ResolveInterface {
    default Optional<Guild> resolveGuild(JDA jda, String name){
        List<Guild> guilds = jda.getGuildsByName(name, true);

        return guilds.size() == 1 ? Optional.of(guilds.get(0)) : Optional.empty();
    }

    default Optional<Guild> resolveGuild(JDA jda, long id){
        return Optional.ofNullable(jda.getGuildById(id));
    }

    default Optional<User> resolveUser(JDA jda, String name){
        List<User> users = jda.getUsersByName(name, true);

        return users.size() == 1 ? Optional.of(users.get(0)) : Optional.empty();
    }

    default Optional<User> resolveUser(JDA jda, long id){
        return Optional.ofNullable(jda.getUserById(id));
    }

    default Optional<TextChannel> resolveChannel(Guild guild, String name){
        List<TextChannel> channels = guild.getTextChannelsByName(name, true);

        return channels.size() == 1 ? Optional.of(channels.get(0)) : Optional.empty();
    }

    default Optional<TextChannel> resolveChannel(Guild guild, long id){
        return Optional.ofNullable(guild.getTextChannelById(id));
    }

    default Optional<Role> resolveRole(Guild guild, String name){
        List<Role> roles = guild.getRolesByName(name, true);

        return roles.size() == 1 ? Optional.of(roles.get(0)) : Optional.empty();
    }

    default Optional<Role> resolveRole(Guild guild, long id){
        return Optional.ofNullable(guild.getRoleById(id));
    }

    default Optional<Member> resolveMember(Guild guild, String name){
        List<Member> members = guild.getMembersByName(name, true);

        return members.size() == 1 ? Optional.of(members.get(0)) : Optional.empty();
    }

    default Optional<Member> resolveMember(Guild guild, long id){
        return Optional.ofNullable(guild.getMemberById(id));
    }
}
