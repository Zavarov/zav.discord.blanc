    /*
 * Copyright (C) 2017 u/Zavarov
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

package vartas.xml;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import vartas.xml.strings.XMLStringMultimap;

/**
 * This class contains all custom configurations for the bot.
 * @author u/Zavarov
 */
public class XMLConfig extends XMLStringMultimap<String>{
    /**
     * @return the amount of shards for the bot.
     */
    public int getDiscordShards(){
        return Integer.parseInt(get("discord_shards").iterator().next());
    }
    /**
     * @return the delay between each status change.
     */
    public int getStatusInterval(){
        return Integer.parseInt(get("status_interval").iterator().next());
    }
    /**
     * @return the delay between each entry in the activity plot.
     */
    public int getActivityInterval(){
        return Integer.parseInt(get("activity_interval").iterator().next());
    }
    /**
     * @return the age at which a message stops being interactive.
     */
    public int getInteractiveMessageAge(){
        return Integer.parseInt(get("interactive_message_age").iterator().next());
    }
    /**
     * @return an invitation link to the support server.
     */
    public String getSupportInvite(){
        return get("support_invite").iterator().next();
        
    }
    /**
     * @return the version of the bot.
     */
    public String getBotName(){
        return get("bot_name").iterator().next();
    }
    /**
     * @return the version of the bot.
     */
    public String getBotVersion(){
        return get("bot_version").iterator().next();
    }
    /**
     * @return an invitation link for this bot.
     */
    public String getBotInvite(){
        return get("bot_invite").iterator().next();
    }
    /**
     * @return the nonterminal that identifies the command.
     */
    public List<String> getCommandIdentifier(){
        return get("command_identifier").stream().collect(Collectors.toList());
    }
    /**
     * @return the nonterminal that identifies the data.
     */
    public List<String> getDataIdentifier(){
        return get("data_identifier").stream().collect(Collectors.toList());
    }
    /**
     * @return the folder where all configuration files are stored.
     */
    public String getDataFolder(){
        return get("data_folder").iterator().next();
    }
    /**
     * @return the prefix every command starts with.
     */
    public String getPrefix(){
        return get("command_prefix").iterator().next();
    }
    /**
     * Creates a new server file from an XML file.
     * @param reference the XML file.
     * @return the server file containing all elements in the XML document.
     */
    public static XMLConfig create(File reference){
        XMLConfig config = new XMLConfig();
        config.putAll(XMLStringMultimap.create(reference));
        return config;
    }
}