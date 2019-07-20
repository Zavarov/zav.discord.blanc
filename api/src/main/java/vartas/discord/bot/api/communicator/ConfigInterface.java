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

package vartas.discord.bot.api.communicator;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import vartas.discord.bot.io.guild.GuildConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public interface ConfigInterface {
    /**
     * A map of all guilds and their respective server files.
     */
    Map<Guild, GuildConfiguration> configs = new HashMap<>();
    /**
     * @param guild the guild we want the server file from.
     * @return the server file that is connected to the guild.
     */
    default GuildConfiguration config(Guild guild){
        if(configs.containsKey(guild)){
            return configs.get(guild);
        }else{
            File target = new File(String.format("guilds/%s.gld", guild.getId()));
            GuildConfiguration config = new GuildConfiguration(target);
            configs.put(guild, config);
            return config;
        }
    }
    /**
     * A wrapper that requests the server of the guild the channel is in.
     * @param channel the text channel of a guild.
     * @return the server file that is connected to the guild of the text channel.
     */
    default GuildConfiguration config(TextChannel channel){
        return config(channel.getGuild());
    }
    /**
     * A wrapper that requests the server of the guild the role is in.
     * @param role the role of a guild.
     * @return the server file that is connected to the guild of the text channel.
     */
    default GuildConfiguration config(Role role){
        return config(role.getGuild());
    }
    /**
     * Deletes the configuration file associated with the guild.
     * @param guild the guild whose XML file is deleted.
     */
    default void delete(Guild guild){
        configs.remove(guild);
        File file = new File(String.format("guilds/%s.gld",guild.getId()));
        if(file.exists())
            file.delete();
    }
}
