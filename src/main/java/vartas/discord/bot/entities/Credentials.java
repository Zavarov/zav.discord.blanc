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

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
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
@Nonnull
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
    @Nonnull
    protected Map<StringType, String> stringMap = new HashMap<>();
    /**
     * The internal {@link Map} containing all key-value pairs over {@link Integer integers}.<br>
     * Expected keys for this map are:
     * <p>
     *     <li>{@link Credentials.IntegerType#STATUS_MESSAGE_UPDATE_INTERVAL STATUS_MESSAGE_UPDATE_INTERVAL}</li>
     *     <li>{@link Credentials.IntegerType#INTERACTIVE_MESSAGE_LIFETIME INTERACTIVE_MESSAGE_LIFETIME}</li>
     *     <li>{@link Credentials.IntegerType#ACTIVITY_UPDATE_INTERVAL ACTIVITY_UPDATE_INTERVAL}</li>
     *     <li>{@link Credentials.IntegerType#IMAGE_WIDTH IMAGE_WIDTH}</li>
     *     <li>{@link Credentials.IntegerType#IMAGE_HEIGHT IMAGE_HEIGHT}</li>
     * </p>
     */
    @Nonnull
    protected Map<IntegerType, Integer> intMap = new HashMap<>();

    /**
     * Associates the specified {@code key} with the specified {@code key} in the {@link #stringMap string map}.<br>
     * If the {@code key} is already used, the old value is replaced.<br>
     * @param key the {@code key} with which the {@code value} is associated
     * @param value the {@code value} with which the {@code key} is associated
     * @throws IllegalArgumentException if the {@code key} is already used in another map
     * @throws NullPointerException if {@code key} or {@code value} is null
     */
    public void setType(@Nonnull StringType key, @Nonnull String value) throws IllegalArgumentException, NullPointerException{
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(value);
        key.check(value);
        stringMap.put(key, value);
    }

    /**
     * Associates the specified {@code key} with the specified {@code key} in the {@link #intMap integer map}.<br>
     * If the {@code key} is already used, the old value is replaced.<br>
     * @param key the {@code key} with which the {@code value} is associated
     * @param value the {@code value} with which the {@code key} is associated
     * @throws IllegalArgumentException if the {@code key} is already used in another map
     * @throws NullPointerException if {@code key} is null

     */
    public void setType(@Nonnull IntegerType key, int value) throws IllegalArgumentException, NullPointerException{
        Preconditions.checkNotNull(key);
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
    @Nonnull
    public String getInviteSupportServer(){
        return stringMap.get(StringType.INVITE_SUPPORT_SERVER);
    }

    /**
     * @return the internal name of this bot.
     */
    @Nonnull
    public String getBotName(){
        return stringMap.get(StringType.BOT_NAME);
    }

    /**
     * The global prefix is universally usable. While a custom prefix is only valid in the guild it was specified,
     * the global prefix can be used in any guild. It is also the only prefix that is valid in private messages.
     * @return the global prefix of this bot.
     */
    @Nonnull
    public String getGlobalPrefix(){
        return stringMap.get(StringType.GLOBAL_PREFIX);
    }

    /**
     * @return an URL listing and explaining the individual commands of this bot.
     */
    @Nonnull
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
    @Nonnull
    public String getDiscordToken(){
        return stringMap.get(StringType.DISCORD_TOKEN);
    }

    /**
     * The account who generated the credentials for the bot. <br>
     * It is part of the {@link net.dean.jraw.http.UserAgent user agent}.
     * @return the name of the Reddit account.
     */
    @Nonnull
    public String getRedditAccount(){
        return stringMap.get(StringType.REDDIT_ACCOUNT);
    }

    /**
     * The internal bot id is necessary for connecting to the Reddit API and should be kept private.
     * It is part of the {@link net.dean.jraw.http.UserAgent user agent}.
     * @return the Reddit id of the bot.
     */
    @Nonnull
    public String getRedditId(){
        return stringMap.get(StringType.REDDIT_ID);
    }

    /**
     * The secret is necessary for verifying that only this program is able to connect to the Reddit API
     * via the given credentials. Obviously is should be kept private.<br>
     * It is part of the {@link net.dean.jraw.http.UserAgent user agent}.
     * @return the Reddit secret of the bot.
     */
    @Nonnull
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
        default void check(T argument) throws IllegalArgumentException{}
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
        @Nonnull
        private String name;
        /**
         * The predicate that checks if the argument is well formed.
         */
        @Nonnull
        private Predicate<Integer> checker;
        /**
         * Initializes the key with its name.
         * @param name the name of the key.
         * @param check the checker for the values.
         * @throws NullPointerException if {@code name} or {@code check} is null
         */
        IntegerType(@Nonnull String name, @Nonnull Predicate<Integer> check) throws NullPointerException{
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(check);
            this.name = name;
            this.checker = check;
        }
        @Nonnull
        @Override
        public String getName(){
            return name;
        }

        /**
         * @throws NullPointerException if {@code argument} is null
         */
        @Override
        public void check(Integer argument) throws IllegalArgumentException {
            Preconditions.checkNotNull(argument);
            if(!checker.test(argument))
                throw new IllegalArgumentException(Integer.toString(argument));
        }
    }

    /**
     * A collection of all keys that are associated with {@link String strings}.
     */
    public enum StringType implements Type<String>{
        /**
         * The invite link to the support server.
         */
        INVITE_SUPPORT_SERVER("InviteSupportServer"),
        /**
         * The internal bot name.
         */
        BOT_NAME("BotName"),
        /**
         * The global prefix for all commands.
         */
        GLOBAL_PREFIX("GlobalPrefix"),
        /**
         * The URL to the command wiki.
         */
        WIKI_LINK("WikiLink"),
        /**
         * The Discord token used by the bot.
         */
        DISCORD_TOKEN("DiscordToken"),
        /**
         * The Reddit user who registered this bot.
         */
        REDDIT_ACCOUNT("RedditAccount"),
        /**
         * The internal Reddit id of the bot.
         */
        REDDIT_ID("RedditId"),
        /**
         * The Reddit secret for this bot.
         */
        REDDIT_SECRET("RedditSecret");
        /**
         * The name of the key in the credentials file.
         */
        private String name;
        /**
         * Initializes the key with its name.
         * @param name the name of the key.
         * @throws NullPointerException if {@code name} is null
         */
        StringType(String name) throws NullPointerException{
            Preconditions.checkNotNull(name);
            this.name = name;
        }

        @Nonnull
        @Override
        public String getName(){
            return name;
        }
    }
}
