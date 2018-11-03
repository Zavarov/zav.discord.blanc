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
package vartas.xml;

import java.io.File;
import vartas.xml.strings.XMLStringMultimap;
import vartas.reddit.RedditCredentials;

/**
 * This class contains all data required to log into the Discord API.
 * @author u/Zavarov
 */
public class XMLCredentials extends XMLStringMultimap<String> implements RedditCredentials{
    /**
     * @return the token on the bot.
     */
    public String getDiscordToken(){
        return get("discord_token").iterator().next();
    }
    /**
     * @return the Reddit id of this bot. 
     */
    @Override
    public String getId() {
        return get("reddit_id").iterator().next();
    }
    /**
     * @return the Reddit secret of this bot. 
     */
    @Override
    public String getSecret() {
        return get("reddit_secret").iterator().next();
    }
    /**
     * @return the identifier for this bot.
     */
    @Override
    public String getAppid() {
        return get("reddit_appid").iterator().next();
    }
    /**
     * @return the owner of the token. 
     */
    @Override
    public String getUser() {
        return get("reddit_user").iterator().next();
    }
    /**
     * @return a redirect link when the access was denied. 
     */
    @Override
    public String getRedirect() {
        return get("reddit_redirect").iterator().next();
    }
    /**
     * @return the permission this bot has.
     */
    @Override
    public String getScope() {
        return get("reddit_scope").iterator().next();
    }
    /**
     * @return the hardware type that the bot is using.
     */
    @Override
    public String getPlatform() {
        return get("reddit_platform").iterator().next();
    }   
    /**
     * @return the version of the Reddit interface 
     */
    @Override
    public String getVersion() {
        return get("reddit_version").iterator().next();
    }
    /**
     * Creates a new server file from an XML file.
     * @param reference the XML file.
     * @return the server file containing all elements in the XML document.
     */
    public static XMLCredentials create(File reference){
        XMLCredentials credentials = new XMLCredentials();
        credentials.putAll(XMLStringMultimap.create(reference));
        return credentials;
    }
}
