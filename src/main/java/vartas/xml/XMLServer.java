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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import vartas.xml.strings.XMLStringMultitable;

/**
 * This class grants access to the configuration file for Discord guilds.
 * @author u/Zavarov
 */
public class XMLServer extends XMLStringMultitable<String,String>{
    /**
     * A helper function that creates a set of all entries at the given position.
     * @param <T> the type of the new entity.
     * @param row the row of the table.
     * @param column the column of the table.
     * @param function a function that transforms the entrys into a different entity.
     * @return a set of all entities at that position.
     */
    private <T> Set<T> getSet(String row, String column, Function<String,T> function){
        if(contains(row,column)){
            List<String> values = get(row,column);
            values.removeIf(e -> function.apply(e) == null);
            
            if(values.isEmpty()){
                remove(row, column);
                return Sets.newHashSet();
            }else{
                return values.stream().map(function).collect(Collectors.toSet());
            }
        }
        return Sets.newHashSet();
    }
    /**
     * A helper function that creates a multimap of all entries at the given position.
     * @param <T> the type of the new entity.
     * @param row the row of the table.
     * @param function a function that transforms the entrys into a different entity.
     * @return a multimap with the column as a key and the entries as a value.
     */
    private <T> Multimap<String,T> getMultimap(String row, Function<String,T> function){
        Multimap<String,T> multimap = HashMultimap.create();
        //Don't use a view in case something gets deleted in getSet()
        new HashSet<>(row(row).keySet()).forEach( c -> {
            Set<T> set = getSet(row, c, function);
            //Don't add it when we don't have values
            if(!set.isEmpty())
                multimap.putAll(c,getSet(row,c,function));
        });
        return multimap;
    }
    /**
     * @param subreddit a subreddit.
     * @param channel the id of a textchannel.
     * @return true if submissions from the subreddit are posted in that channel.
     */
    public boolean containsRedditFeed(String subreddit, TextChannel channel){
        return contains("reddit",subreddit,channel.getId());
    }
    /**
     * Links a subreddit to a channel. New submissions will then be posted in there.
     * @param subreddit a subreddit.
     * @param channel a textchannel.
     */
    public void addRedditFeed(String subreddit, TextChannel channel){
        putSingle("reddit",subreddit,channel.getId());
    }
    /**
     * Removes the link between a subreddit and a textchannel.
     * @param subreddit the subreddit.
     * @param channel the channel.
     */
    public void removeRedditFeed(String subreddit, TextChannel channel){
        removeSingle("reddit", subreddit, channel.getId());
    }
    /**
     * Returns all feeds that are active in this server.
     * @param guild the guild this file is associated with.
     * @return a map of all subreddit-textchannel pairs.
     */
    public Multimap<String, TextChannel> getRedditFeeds(Guild guild){
        return getMultimap("reddit",guild::getTextChannelById);
    }
    /**
     * @param guild the guild this file is associated with.
     * @param subreddit the subreddit.
     * @return all channel that are linked to the specified subreddit..
     */
    public Set<TextChannel> getRedditFeed(Guild guild, String subreddit){
        return getSet("reddit",subreddit, guild::getTextChannelById);
    }
    /**
     * @param expression an expression.
     * @return true if the expression is in the list of filtered word.
     */
    public boolean isFiltered(String expression){
        return contains("filter","word",expression);
    }
    /**
     * Adds an expression to the list of filtered words.
     * @param expression an expression.
     */
    public void addFilter(String expression){
        putSingle("filter","word",expression);
    }
    /**
     * Removes an expression from the list of filtered words.
     * @param expression 
     */
    public void removeFilter(String expression){
        removeSingle("filter","word",expression);
    }
    /**
     * @return all filtered words.
     */
    public Set<String> getFilter(){
        return getSet("filter","word",o -> o);
    }
    /**
     * @param role a role.
     * @return true if the role is in a group.
     */
    public boolean isTagged(Role role){
        return row("role").values().stream()
                .flatMap(e -> e.stream())
                .anyMatch(e -> role.getId().equals(e));
    }
    /**
     * Adds a role to a group of roles.
     * @param tag the tag of the group.
     * @param role the role.
     */
    public void tag(String tag, Role role){
        if(!isTagged(role))
            putSingle("role", tag, role.getId());
    }
    /**
     * Removes a role from a group of roles.
     * @param role the role.
     */
    public void untag(Role role){
        if(isTagged(role))
            removeSingle("role", getTag(role), role.getId());
    }
    /**
     * @param role a role.
     * @return the group tag of this role or null if this role isn't grouped.
     */
    public String getTag(Role role){
        return row("role").entrySet().stream()
                .filter(e -> e.getValue().contains(role.getId()))
                .map(e -> e.getKey())
                .findFirst()
                .orElse(null);
    }
    /**
     * @param guild the guild this file is associated with.
     * @return a map of all groups.
     */
    public Multimap<String, Role> getTags(Guild guild){
        return getMultimap("role", guild::getRoleById);
    }
    /**
     * @return the custom prefix for this server or null if no such prefix
     * exists.
     */
    public String getPrefix(){
        if(hasPrefix()){
            return get("server","prefix").iterator().next();
        }else{
            return null;
        }
    }
    /**
     * Sets a custom prefix for the server and overwrites any
     * older ones. The bot will react to any command that starts with the
     * default prefix and this custom prefix.
     * Using null will only remove the old prefix.
     * @param prefix the new prefix
     */
    public void setPrefix(String prefix){
        remove("server","prefix");
        if(prefix != null)
            putSingle("server","prefix",prefix);
    }
    /**
     * @return true if the guild has a custom prefix. 
     */
    public boolean hasPrefix(){
        return contains("server","prefix");
    }
    /**
     * Creates a new server file from an XML file.
     * @param reference the XML file.
     * @return the server file containing all elements in the XML document.
     */
    public static XMLServer create(File reference){
        XMLServer server = new XMLServer();
        server.putAll(XMLStringMultitable.create(reference));
        return server;
    }
}