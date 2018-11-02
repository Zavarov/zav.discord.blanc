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
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.User;
import vartas.discordbot.command.Rank;
import vartas.xml.strings.XMLStringMultimap;

/**
 * This class grants access to the permission file containing developers and user with root permissions.
 * @author u/Zavarov
 */
public class XMLPermission extends XMLStringMultimap<String>{
    /**
     * Gives the user the specified rank.
     * @param rank the rank in question.
     * @param user the user in question.
     */
    public void add(Rank rank, User user){
        put(rank.name(),user.getId());
    }
    /**
     * Removes the rank from the specified user.
     * @param rank the rank in question.
     * @param user the user in question.
     */
    public void remove(Rank rank, User user){
        remove(rank.name(),user.getId());
    }
    /**
     * 
     * @param user the user in question.
     * @return the set of all ranks the user has.
     */
    public Set<Rank> getRanks(User user){
        Set<Rank> ranks = super.asMap()
                .entrySet()
                .stream()
                .filter(e -> e.getValue().contains(user.getId()))
                .map(e -> Rank.valueOf(e.getKey()))
                .collect(Collectors.toCollection(HashSet::new));
        ranks.add(Rank.USER);
        return ranks;
        
    }
    /**
     * Creates a new permission map from an XML file.
     * @param reference the XML file.
     * @return the permissions containing all elements in the XML document.
     */
    public static XMLPermission create(File reference){
        XMLPermission permission = new XMLPermission();
        permission.putAll(XMLStringMultimap.create(reference));
        return permission;
    }
}