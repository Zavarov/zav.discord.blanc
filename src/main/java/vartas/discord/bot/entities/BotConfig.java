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
    protected Map<Type, Long> longMap = Maps.newConcurrentMap();

    public void setType(Type key, String value){
        if(!longMap.containsKey(key))
            stringMap.put(key, value);
    }

    public void setType(Type key, long value){
        if(!stringMap.containsKey(key))
            longMap.put(key, value);
    }

    public long getStatusMessageUpdateInterval(){
        return longMap.get(Type.STATUS_MESSAGE_UPDATE_INTERVAL);
    }

    public long getDiscordShards(){
        return longMap.get(Type.DISCORD_SHARDS);
    }

    public long getInteractiveMessageLifetime(){
        return longMap.get(Type.INTERACTIVE_MESSAGE_LIFETIME);
    }

    public long getActivityUpdateInterval(){
        return longMap.get(Type.ACTIVITY_UPDATE_INTERVAL);
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

    public long getImageWidth(){
        return longMap.get(Type.IMAGE_WIDTH);
    }

    public long getImageHeight(){
        return longMap.get(Type.IMAGE_HEIGHT);
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
        STATUS_MESSAGE_UPDATE_INTERVAL,
        DISCORD_SHARDS,
        INTERACTIVE_MESSAGE_LIFETIME,
        ACTIVITY_UPDATE_INTERVAL,
        INVITE_SUPPORT_SERVER,
        BOT_NAME,
        GLOBAL_PREFIX,
        WIKI_LINK,
        IMAGE_WIDTH,
        IMAGE_HEIGHT,
        DISCORD_TOKEN,
        REDDIT_ACCOUNT,
        REDDIT_ID,
        REDDIT_SECRET;
    }
}
