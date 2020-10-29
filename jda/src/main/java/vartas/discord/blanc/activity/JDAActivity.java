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

package vartas.discord.blanc.activity;

import net.dv8tion.jda.api.OnlineStatus;
import vartas.discord.blanc.Guild;
import vartas.discord.blanc.TextChannel;
import vartas.discord.blanc.visitor.ArchitectureVisitor;

import java.time.LocalDateTime;

public class JDAActivity extends Activity{
    private final net.dv8tion.jda.api.entities.Guild jdaGuild;
    public JDAActivity(net.dv8tion.jda.api.entities.Guild jdaGuild){
        this.jdaGuild = jdaGuild;
    }

    @Override
    public void update(Guild guild){
        putActivity(LocalDateTime.now(), new GuildVisitor().gather(guild));
    }

    private class GuildVisitor implements ArchitectureVisitor{
        GuildActivity data = new GuildActivity();

        private GuildVisitor(){}

        public GuildActivity gather(Guild guild){
            GuildVisitor visitor = new GuildVisitor();
            guild.accept(visitor);
            return data;
        }

        @Override
        public void visit(TextChannel channel){
            //TODO Magic number. 5 is the time in minutes after which messages expire.
            data.putChannelActivity(channel, channel.valuesMessages().size() / 5.0);
        }

        @Override
        public void endVisit(Guild guild){
            data.setMembersOnline(jdaGuild.getMembers().stream().filter(member -> member.getOnlineStatus() == OnlineStatus.ONLINE).count());
            data.setMembersCount(jdaGuild.getMemberCount());
            data.setActivity(data.valuesChannelActivity().stream().mapToDouble(x -> x).sum());
        }

    }
}
