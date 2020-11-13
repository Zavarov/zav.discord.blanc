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

package vartas.discord.blanc.mock;

import com.google.common.base.Preconditions;
import vartas.discord.blanc.$factory.GuildFactory;
import vartas.discord.blanc.$factory.MessageEmbedFactory;
import vartas.discord.blanc.$factory.TitleFactory;
import vartas.discord.blanc.*;

import javax.annotation.Nonnull;
import java.util.*;

public class GuildMock extends Guild {
    public Map<Long, Member> members = new HashMap<>();
    public Map<Long, Role> roles = new HashMap<>();
    public Map<Long, TextChannel> channels = new HashMap<>();
    public SelfMember selfMember;

    public GuildMock(){}

    public GuildMock(long id, String name){
        GuildFactory.create(() -> this, new ActivityMock(), id, name);
    }

    @Override
    public void leave(){
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canInteract(@Nonnull Member member, @Nonnull Role role) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean canInteract(@Nonnull Member member, @Nonnull TextChannel textChannel) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MessageEmbed toMessageEmbed(){
        return MessageEmbedFactory.create(
                Optional.empty(),
                Optional.empty(),
                Optional.of(TitleFactory.create("Guild")),
                Optional.of(Long.toUnsignedString(getId())),
                Optional.empty(),
                Optional.empty(),
                Collections.emptyList()
        );
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


    @Override
    public Optional<Member> retrieveMember(long id) {
        return Optional.ofNullable(members.get(id));
    }

    @Override
    public Collection<Member> retrieveMembers() {
        return members.values();
    }

    @Override
    public Optional<TextChannel> retrieveTextChannel(long id) {
        return Optional.ofNullable(channels.get(id));
    }

    @Override
    public Collection<TextChannel> retrieveTextChannels() {
        return channels.values();
    }

    @Override
    public Optional<Role> retrieveRole(long id) {
        return Optional.ofNullable(roles.get(id));
    }

    @Override
    public Collection<Role> retrieveRoles() {
        return roles.values();
    }

    @Override
    public SelfMember retrieveSelfMember() {
        return Preconditions.checkNotNull(selfMember);
    }
}
