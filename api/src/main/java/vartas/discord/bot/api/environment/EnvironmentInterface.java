/*
 * Copyright (C) 2018 u/Zavarov
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
package vartas.discord.bot.api.environment;

import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.io.config._ast.ASTConfigArtifact;
import vartas.discord.bot.io.rank.RankConfiguration;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This interface is intended to be used as a tool to deal with the communicators
 * for each shard.
 */
public interface EnvironmentInterface extends RedditInterface{
    /**
     * The logger for the environment.
     */
    Logger log = JDALogger.getLog(EnvironmentInterface.class.getSimpleName());
    /**
     * @return the configuration file.
     */
    ASTConfigArtifact config();
    /**
     * @return rank file.
     */
    RankConfiguration rank();
    /**
     * @param guild a guild instance.
     * @return the bot that this guild belongs to. 
     */
    default CommunicatorInterface communicator(Guild guild){
        return communicator(guild.getId());
    }
    /**
     * @param channel a text channel inside a guild.
     * @return the bot that this guild belongs to. 
     */
    default CommunicatorInterface communicator(TextChannel channel){
        return communicator(channel.getGuild());
    }
    /**
     * @param role a role inside a guild.
     * @return the bot that this guild belongs to. 
     */
    default CommunicatorInterface communicator(Role role){
        return communicator(role.getGuild());
    }
    /**
     * @param id the id of the guild.
     * @return the bot that this guild belongs to. 
     * @throws NumberFormatException if the string can't be parsed as a long.
     */
    default CommunicatorInterface communicator(String id) throws NumberFormatException{
        return communicator(Long.parseLong(id));
    }
    /**
     * @param id the id of the guild.
     * @return the bot that this guild belongs to. 
     */
    CommunicatorInterface communicator(long id);
    /**
     * Returns all JDAs that are registered in this environment.
     */
    List<JDA> jdas();
    /**
     * Returns all guilds that are registered in the environment.
     * Since a guild only exists in a single shard, this means that the result will be an aggregation over all shards.
     */
    default List<Guild> guilds(){
        return jdas().stream().map(JDA::getGuilds).flatMap(Collection::stream).collect(Collectors.toList());
    }
    /**
     * Attempts to shutdown all communicators.
     * @return the task that will await the shutdown of all communicators.
     */
    Runnable shutdown();
}