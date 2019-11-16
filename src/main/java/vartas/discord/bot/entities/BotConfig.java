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

import com.google.common.collect.Maps;

import java.util.Map;

public class BotConfig {
    protected Map<Type, String> stringMap = Maps.newConcurrentMap();
    protected Map<Type, Integer> intMap = Maps.newConcurrentMap();

    public void setType(Type key, String value){
        if(!intMap.containsKey(key))
            stringMap.put(key, value);
    }

    public void setType(Type key, int value){
        if(!stringMap.containsKey(key))
            intMap.put(key, value);
    }

    public int getStatusMessageUpdateInterval(){
        return intMap.get(Type.STATUS_MESSAGE_UPDATE_INTERVAL);
    }

    public int getDiscordShards(){
        return intMap.get(Type.DISCORD_SHARDS);
    }

    public int getInteractiveMessageLifetime(){
        return intMap.get(Type.INTERACTIVE_MESSAGE_LIFETIME);
    }

    public int getActivityUpdateInterval(){
        return intMap.get(Type.ACTIVITY_UPDATE_INTERVAL);
    }

    public String getInviteSupportServer(){
        return stringMap.get(Type.INVITE_SUPPORT_SERVER);
    }

    public String getBotName(){
        return stringMap.get(Type.BOT_NAME);
    }

    public String getGlobalPrefix(){
        return stringMap.get(Type.GLOBAL_PREFIX);
    }

    public String getWikiLink(){
        return stringMap.get(Type.WIKI_LINK);
    }

    public int getImageWidth(){
        return intMap.get(Type.IMAGE_WIDTH);
    }

    public int getImageHeight(){
        return intMap.get(Type.IMAGE_HEIGHT);
    }

    public String getDiscordToken(){
        return stringMap.get(Type.DISCORD_TOKEN);
    }

    public String getRedditAccount(){
        return stringMap.get(Type.REDDIT_ACCOUNT);
    }

    public String getRedditId(){
        return stringMap.get(Type.REDDIT_ID);
    }

    public String getRedditSecret(){
        return stringMap.get(Type.REDDIT_SECRET);
    }

    public enum Type{
        STATUS_MESSAGE_UPDATE_INTERVAL("StatusMessageUpdateInterval"),
        DISCORD_SHARDS("DiscordShards"),
        INTERACTIVE_MESSAGE_LIFETIME("InteractiveMessageLifetime"),
        ACTIVITY_UPDATE_INTERVAL("ActivityUpdateInterval"),
        INVITE_SUPPORT_SERVER("InviteSupportServer"),
        BOT_NAME("BotName"),
        GLOBAL_PREFIX("GlobalPrefix"),
        WIKI_LINK("WikiLink"),
        IMAGE_WIDTH("ImageWidth"),
        IMAGE_HEIGHT("ImageHeight"),
        DISCORD_TOKEN("DiscordToken"),
        REDDIT_ACCOUNT("RedditAccount"),
        REDDIT_ID("RedditId"),
        REDDIT_SECRET("RedditSecret");

        private String name;

        Type(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }
    }
}
