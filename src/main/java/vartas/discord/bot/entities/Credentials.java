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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * This class contains the configuration of the Discord bot, like name, global prefix,
 * number of shards and so on.<br>
 * Additionally, it also contains all the credentials for the Discord and Reddit API.<br>
 * This file is structured like a map, where the keys are elements of {@link Credentials.Type},
 * and the values are either {@link String strings} or {@link Integer integers}.<br>
 * Internally, we use two maps, one for each of the value types. <br>
 * For each map, we have an enum with valid keys. In addition, each key checks if the value that is associated with it
 * is valid. For an integer, this could be a condition that the value has to be strictly positive. But for a more
 * detailed description, please look at the individual keys.<br>
 * If the condition is not met, an {@link IllegalArgumentException} is thrown.
 */
public class Credentials {
    /**
     * The internal {@link Map} containing all key-value pairs over {@link String strings}.<br>
     * Valid keys for this map are:
     * <p>
     *     <li>{@link Credentials.StringType#INVITE_SUPPORT_SERVER INVITE_SUPPORT_SERVER}</li>
     *     <li>{@link Credentials.StringType#BOT_NAME BOT_NAME}</li>
     *     <li>{@link Credentials.StringType#GLOBAL_PREFIX GLOBAL_PREFIX}</li>
     *     <li>{@link Credentials.StringType#WIKI_LINK WIKI_LINK}</li>
     *     <li>{@link Credentials.StringType#DISCORD_TOKEN DISCORD_TOKEN}</li>
     *     <li>{@link Credentials.StringType#REDDIT_ACCOUNT REDDIT_ACCOUNT}</li>
     *     <li>{@link Credentials.StringType#REDDIT_ID REDDIT_ID}</li>
     *     <li>{@link Credentials.StringType#REDDIT_SECRET REDDIT_SECRET}</li>
     * </p>
     */
    protected Map<StringType, String> stringMap = new HashMap<>();
    /**
     * The internal {@link Map} containing all key-value pairs over {@link Integer integers}.<br>
     * Expected keys for this map are:
     * <p>
     *     <li>{@link Credentials.IntegerType#STATUS_MESSAGE_UPDATE_INTERVAL STATUS_MESSAGE_UPDATE_INTERVAL}</li>
     *     <li>{@link Credentials.IntegerType#DISCORD_SHARDS DISCORD_SHARDS}</li>
     *     <li>{@link Credentials.IntegerType#INTERACTIVE_MESSAGE_LIFETIME INTERACTIVE_MESSAGE_LIFETIME}</li>
     *     <li>{@link Credentials.IntegerType#ACTIVITY_UPDATE_INTERVAL ACTIVITY_UPDATE_INTERVAL}</li>
     *     <li>{@link Credentials.IntegerType#IMAGE_WIDTH IMAGE_WIDTH}</li>
     *     <li>{@link Credentials.IntegerType#IMAGE_HEIGHT IMAGE_HEIGHT}</li>
     * </p>
     */
    protected Map<IntegerType, Integer> intMap = new HashMap<>();

    /**
     * Associates the specified {@code key} with the specified {@code key} in the {@link #stringMap string map}.<br>
     * If the {@code key} is already used, the old value is replaced.<br>
     * @param key the {@code key} with which the {@code value} is associated
     * @param value the {@code value} with which the {@code key} is associated
     * @throws IllegalArgumentException if the {@code key} is already used in another map
     */
    public void setType(StringType key, String value) throws IllegalArgumentException{
        key.check(value);
        stringMap.put(key, value);
    }

    /**
     * Associates the specified {@code key} with the specified {@code key} in the {@link #intMap integer map}.<br>
     * If the {@code key} is already used, the old value is replaced.<br>
     * @param key the {@code key} with which the {@code value} is associated
     * @param value the {@code value} with which the {@code key} is associated
     * @throws IllegalArgumentException if the {@code key} is already used in another map

     */
    public void setType(IntegerType key, int value) throws IllegalArgumentException{
        key.check(value);
        intMap.put(key, value);
    }

    /**
     * The interval is given in minutes.
     * @return the interval with which the status message is updated
     */
    public int getStatusMessageUpdateInterval(){
        return intMap.get(IntegerType.STATUS_MESSAGE_UPDATE_INTERVAL);
    }

    /**
     * The total number of shards has to be a positive value.<br>
     * Each individual shard will then be within [0, ({@code shards} - 1)]
     * @return the number of Discord shards
     */
    public int getDiscordShards(){
        return intMap.get(IntegerType.DISCORD_SHARDS);
    }

    /**
     * The lifetime is given in minutes.
     * @return the maximum age an interactive message can have before the bot stops reacting to it
     */
    public int getInteractiveMessageLifetime(){
        return intMap.get(IntegerType.INTERACTIVE_MESSAGE_LIFETIME);
    }

    /**
     * The interval is given in minutes.
     * @return the interval with which the total members count and the member that are online in the activity chart
     *         are updated.<br>
     *         Even though it does not change the way messages are received, in the generated chart,
     *         all messages that are received within the given interval are accumulated.
     */
    public int getActivityUpdateInterval(){
        return intMap.get(IntegerType.ACTIVITY_UPDATE_INTERVAL);
    }

    /**
     * @return the URL required for joining the Discord support server for this bot.
     */
    public String getInviteSupportServer(){
        return stringMap.get(StringType.INVITE_SUPPORT_SERVER);
    }

    /**
     * @return the internal name of this bot.
     */
    public String getBotName(){
        return stringMap.get(StringType.BOT_NAME);
    }

    /**
     * The global prefix is universally usable. While a custom prefix is only valid in the guild it was specified,
     * the global prefix can be used in any guild. It is also the only prefix that is valid in private messages.
     * @return the global prefix of this bot.
     */
    public String getGlobalPrefix(){
        return stringMap.get(StringType.GLOBAL_PREFIX);
    }

    /**
     * @return an URL listing and explaining the individual commands of this bot.
     */
    public String getWikiLink(){
        return stringMap.get(StringType.WIKI_LINK);
    }

    /**
     * The image width is given in pixels.
     * @return the width of all generated images.
     */
    public int getImageWidth(){
        return intMap.get(IntegerType.IMAGE_WIDTH);
    }

    /**
     * The image height is given in pixels.
     * @return the height of all generated images.
     */
    public int getImageHeight(){
        return intMap.get(IntegerType.IMAGE_HEIGHT);
    }

    /**
     * The token is required for connecting to the Discord API and should be kept private.
     * @return the Discord token.
     */
    public String getDiscordToken(){
        return stringMap.get(StringType.DISCORD_TOKEN);
    }

    /**
     * The account who generated the credentials for the bot. <br>
     * It is part of the {@link net.dean.jraw.http.UserAgent user agent}.
     * @return the name of the Reddit account.
     */
    public String getRedditAccount(){
        return stringMap.get(StringType.REDDIT_ACCOUNT);
    }

    /**
     * The internal bot id is necessary for connecting to the Reddit API and should be kept private.
     * It is part of the {@link net.dean.jraw.http.UserAgent user agent}.
     * @return the Reddit id of the bot.
     */
    public String getRedditId(){
        return stringMap.get(StringType.REDDIT_ID);
    }

    /**
     * The secret is necessary for verifying that only this program is able to connect to the Reddit API
     * via the given credentials. Obviously is should be kept private.<br>
     * It is part of the {@link net.dean.jraw.http.UserAgent user agent}.
     * @return the Reddit secret of the bot.
     */
    public String getRedditSecret(){
        return stringMap.get(StringType.REDDIT_SECRET);
    }

    /**
     * A wrapper interface to combine the keys from {@link IntegerType} and {@link StringType}.<br>
     * @param <T> the type of values this key can accept.
     */
    public interface Type <T>{
        /**
         * @return the name of the key in the credentials file.
         */
        String getName();

        /**
         * Checks if the argument is well formed. This can mean that it is not null, or a more specific restriction for
         * the individual types it can be.
         * @param argument the value associated with this key.
         * @throws IllegalArgumentException if the argument is malformed.
         */
        void check(T argument) throws IllegalArgumentException;
    }

    /**
     * A collection of all keys that are associated with {@link Integer integers}.
     */
    public enum IntegerType implements Type<Integer>{
        /**
         * Indicates the interval in minutes with which the status message of the bots in
         * all shards is updated.<br>
         * Has to be positive.
         */
        STATUS_MESSAGE_UPDATE_INTERVAL("StatusMessageUpdateInterval", value -> value > 0),
        /**
         * The total number of shards Discord is partitioned in.<br>
         * Has to be positive.
         */
        DISCORD_SHARDS("DiscordShards", value -> value > 0),
        /**
         * The maximum age since the last modification of an interactive message until the
         * bot stops interacting with it.<br>
         * Has to be positive.
         */
        INTERACTIVE_MESSAGE_LIFETIME("InteractiveMessageLifetime", value -> value > 0),
        /**
         * The step size between the different entries in the activity chart.<br>
         * Has to be positive.
         */
        ACTIVITY_UPDATE_INTERVAL("ActivityUpdateInterval", value -> value > 0),
        /**
         * The image width in pixels.<br>
         * Has to be positive.
         */
        IMAGE_WIDTH("ImageWidth", value -> value > 0),
        /**
         * The image height in pixels.<br>
         * Has to be positive.
         */
        IMAGE_HEIGHT("ImageHeight", value -> value > 0);
        /**
         * The name of the key in the credentials file.
         */
        private String name;
        /**
         * The predicate that checks if the argument is well formed.
         */
        private Predicate<Integer> checker;
        /**
         * Initializes the key with its name.
         * @param name the name of the key.
         * @param check the checker for the values.
         */
        IntegerType(String name, Predicate<Integer> check){
            this.name = name;
            this.checker = check;
        }

        @Override
        public String getName(){
            return name;
        }

        @Override
        public void check(Integer argument) throws IllegalArgumentException {
            if(!checker.test(argument))
                throw new IllegalArgumentException(Integer.toString(argument));
        }
    }

    /**
     * A collection of all keys that are associated with {@link String strings}.
     */
    public enum StringType implements Type<String>{
        /**
         * The invite link to the support server.<br>
         * Can't be {@code null}.
         */
        INVITE_SUPPORT_SERVER("InviteSupportServer", Objects::nonNull),
        /**
         * The internal bot name.<br>
         * Can't be {@code null}.
         */
        BOT_NAME("BotName", Objects::nonNull),
        /**
         * The global prefix for all commands.<br>
         * Can't be {@code null}.
         */
        GLOBAL_PREFIX("GlobalPrefix", Objects::nonNull),
        /**
         * The URL to the command wiki.<br>
         * Can't be {@code null}.
         */
        WIKI_LINK("WikiLink", Objects::nonNull),
        /**
         * The Discord token used by the bot.<br>
         * Can't be {@code null}.
         */
        DISCORD_TOKEN("DiscordToken", Objects::nonNull),
        /**
         * The Reddit user who registered this bot.<br>
         * Can't be {@code null}.
         */
        REDDIT_ACCOUNT("RedditAccount", Objects::nonNull),
        /**
         * The internal Reddit id of the bot.<br>
         * Can't be {@code null}.
         */
        REDDIT_ID("RedditId", Objects::nonNull),
        /**
         * The Reddit secret for this bot.<br>
         * Can't be {@code null}.
         */
        REDDIT_SECRET("RedditSecret", Objects::nonNull);
        /**
         * The name of the key in the credentials file.
         */
        private String name;
        /**
         * The predicate that checks if the argument is well formed.
         */
        private Predicate<String> checker;
        /**
         * Initializes the key with its name.
         * @param name the name of the key.
         * @param check the checker for the values.
         */
        StringType(String name, Predicate<String> check){
            this.name = name;
            this.checker = check;
        }

        @Override
        public String getName(){
            return name;
        }

        @Override
        public void check(String argument) throws IllegalArgumentException {
            if(!checker.test(argument))
                throw new IllegalArgumentException(argument);
        }
    }
}
