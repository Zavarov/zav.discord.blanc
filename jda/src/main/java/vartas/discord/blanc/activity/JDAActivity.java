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
import vartas.discord.blanc.$visitor.ArchitectureVisitor;
import vartas.discord.blanc.Guild;
import vartas.discord.blanc.TextChannel;

import java.time.LocalDateTime;

import static vartas.discord.blanc.Shard.ACTIVITY_RATE;

public class JDAActivity extends Activity{
    private final net.dv8tion.jda.api.entities.Guild jdaGuild;
    public JDAActivity(net.dv8tion.jda.api.entities.Guild jdaGuild){
        this.jdaGuild = jdaGuild;
    }

    @Override
    public void update(Guild guild){
        putActivity(LocalDateTime.now(), new GuildVisitor().gather(guild));
        messages.clear();
    }

    private class GuildVisitor implements ArchitectureVisitor{
        GuildActivity data = new GuildActivity();

        private GuildVisitor(){}

        public GuildActivity gather(Guild guild){
            guild.accept(this);
            return data;
        }

        @Override
        public void visit(TextChannel channel){
            data.putChannelActivity(channel, messages.getOrDefault(channel, 0L) / (double)ACTIVITY_RATE.toMinutes());
        }

        @Override
        public void endVisit(Guild guild){
            data.setMembersOnline(jdaGuild.getMembers().stream().filter(member -> member.getOnlineStatus() == OnlineStatus.ONLINE).count());
            data.setMembersCount(jdaGuild.getMemberCount());
            data.setActivity(data.valuesChannelActivity().stream().mapToDouble(x -> x).sum());
        }

    }
}
