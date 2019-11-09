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

package vartas.discord.bot.entities;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.internal.utils.JDALogger;
import net.dv8tion.jda.internal.utils.cache.UpstreamReference;
import org.slf4j.Logger;
import vartas.discord.bot.entities.guild.Blacklist;
import vartas.discord.bot.entities.guild.Prefix;
import vartas.discord.bot.entities.guild.RoleGroup;
import vartas.discord.bot.entities.guild.SubredditGroup;
import vartas.discord.bot.visitor.BotGuildVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BotGuild {
    /**
     * The logger for this configuration file.
     */
    protected Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    protected Path reference;
    protected Prefix prefix;
    protected Blacklist blacklist;
    protected RoleGroup roleGroup;
    protected SubredditGroup subredditGroup;
    protected UpstreamReference<Guild> guild;

    public BotGuild(Guild guild, DiscordCommunicator communicator){
        this.guild = new UpstreamReference<>(guild);
        this.prefix = new Prefix(guild, communicator);
        this.blacklist = new Blacklist(guild, communicator);
        this.roleGroup = new RoleGroup(guild, communicator);
        this.subredditGroup = new SubredditGroup(guild, communicator);
        this.reference = Paths.get("guilds/"+guild.getId()+".gld");
    }

    public synchronized void accept(BotGuildVisitor visitor){
        visitor.handle(subredditGroup);
        visitor.handle(roleGroup);
        visitor.handle(blacklist);
        visitor.handle(prefix);
    }

    @Override
    public synchronized String toString(){
        StringBuilder builder = new StringBuilder();

        //Body Start
        builder.append("guild ").append(guild.get().getId()).append("L {\n");

        builder.append(prefix.toString());
        builder.append(blacklist.toString());
        builder.append(roleGroup.toString());
        builder.append(subredditGroup.toString());

        //Body End
        builder.append("}");

        return builder.toString();
    }

    public String getId(){
        return guild.get().getId();
    }

    public synchronized void store(){
        try {
            if(!Files.exists(reference.getParent()))
                Files.createDirectory(reference.getParent());

            Files.write(reference, toString().getBytes());
        }catch(IOException e){
            log.error(e.getMessage(), e);
        }
    }

    public synchronized void delete(){
        try {
            Files.deleteIfExists(reference);
        }catch(IOException e){
            log.error(e.getMessage(), e);
        }
    }
}
