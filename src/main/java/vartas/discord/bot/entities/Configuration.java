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
import com.google.common.collect.*;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import vartas.discord.bot.RedditFeed;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * This class contains all guild-specific configurations of the Discord bot.
 * Those consist of:
 * <p>
 *      <li>The prefix</li>
 *      <li>The pattern for matching all illegal words</li>
 *      <li>All self-assignable roles and their respective groups</li>
 *      <li>All textchannels and their respective subreddit, where new submissions are posted in</li>
 * </p>
 */
@Nonnull
public class Configuration {
    /**
     * The id of the guild associated with this configuration file.
     */
    protected final long guildId;
    /**
     * The table containing all groups of Discord ids.<br>
     * The row consists of the type of group. It is used to uniquely identify the groups and is also
     * used to resolve the ids back into instances.<br>
     * The last step is required for validation. In case a entity is no longer in a guild, which happens when a role
     * or text channel gets deleted, the change should be reflected in this file.<br>
     * This validation should be lazy and only done when the instance of this class is modified.<br>
     * The column consists of the name of the group. For each row, the column names should be unique.<br>
     * The value is a set of all ids in the group.<br>
     * Internally, we store this table as a {@link Map} of {@link Multimap Multimaps}, since the default {@link Table}
     * implementation assumes the values to be singletons, and not collections.
     */
    @Nonnull
    protected final Map<LongType, Multimap<String, Long>> groups = new EnumMap<>(LongType.class);
    /**
     * The pattern for all blacklisted words. This value being null indicates that there
     * are no blacklisted words.
     */
    @Nullable
    protected Pattern pattern = null;
    /**
     * The guild prefix. In addition to the global prefix, all commands starting with the guild prefix are accepted, but
     * only inside this guild. This value being null indicates that there is no guild prefix.
     */
    @Nullable
    protected String prefix = null;

    /**
     * Initializes an empty configuration file.
     * @param guildId the id of the guild associated with this instance
     */
    public Configuration(long guildId){
        this.guildId = guildId;
    }

    /**
     * The id of this instance is equivalent to {@link Guild#getId()}.
     * @return the unique id of this instance.
     */
    public long getGuildId(){
        return guildId;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Blacklist                                                                                                    //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Sets the pattern of this guild. All messages whose content is matched by this pattern
     * are automatically deleted.<br>
     * If this guild already has a pattern, it is replaced by the new one.
     * @param pattern the new pattern for this guild
     * @throws NullPointerException if {@code pattern} is null
     */
    public void setPattern(@Nonnull Pattern pattern) throws NullPointerException{
        Preconditions.checkNotNull(pattern);
        this.pattern = pattern;
    }

    /**
     * Removes the current pattern. After this, all messages are accepted again.
     */
    public void removePattern(){
        this.pattern = null;
    }

    /**
     * To avoid the usage of {@code null}, we use an {@link Optional} to indicate the absence of a pattern.
     * @return an {@link Optional} with the current pattern if it exists.
     *         Otherwise {@link Optional#empty()}.
     */
    @Nonnull
    public Optional<Pattern> getPattern(){
        return Optional.ofNullable(pattern);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Prefix                                                                                                       //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Sets the guild prefix. All commands in this guild either have to start
     * with the global prefix or the guild prefix.<br>
     * If this guild already has a prefix, it is replaced by the new one.
     * @param prefix the new guild prefix
     * @throws NullPointerException if {@code prefix} is null
     */
    public void setPrefix(@Nonnull String prefix) throws NullPointerException{
        Preconditions.checkNotNull(prefix);
        this.prefix = prefix;
    }

    /**
     * Removes the guild prefix. After this, all commands have to start with the global prefix.
     */
    public void removePrefix(){
        this.prefix = null;
    }

    /**
     * To avoid the usage of {@code null}, we use an {@link Optional} to indicate the absence of a guild prefix.
     * @return an {@link Optional} with the guild prefix if it exists.
     *         Otherwise {@link Optional#empty()}.
     */
    @Nonnull
    public Optional<String> getPrefix(){
        return Optional.ofNullable(prefix);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Role Group                                                                                                   //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks if the role exists in the specified group.
     * @param column the group name the role is associated with
     * @param value the role associated with the group name
     * @return true if there exists a role in the specified group
     * @throws NullPointerException if {@code column} or {@code value} is null
     */
    public synchronized boolean resolve(@Nonnull String column, @Nonnull Role value) throws NullPointerException{
        Preconditions.checkNotNull(column);
        Preconditions.checkNotNull(value);
        return resolve(LongType.SELFASSIGNABLE, column, value.getIdLong());
    }

    /**
     * Checks if the specified {@code role} is within a group and returns the name associated with it.
     * @param role the specified role
     * @return an {@link Optional} with the group name associated with the role.
     *         If no such association exists, {@link Optional#empty()} is returned.
     * @throws NullPointerException {@code role} is null
     * @throws IllegalStateException if {@code role} exists in more than one group
     */
    @Nonnull
    public synchronized Optional<String> resolve(@Nonnull Role role) throws NullPointerException, IllegalStateException{
        Preconditions.checkNotNull(role);
        Set<String> keys = resolve(LongType.SELFASSIGNABLE, role.getIdLong());

        //A role is in at most one group
        Preconditions.checkArgument(keys.size() <= 1);

        return keys.stream().findAny();
    }

    /**
     * Removes the mapping for an entry in the underlying table.
     * @param role the role associated with a group name
     * @throws NullPointerException if {@code role} is null
     */
    public synchronized void remove(@Nonnull Role role) throws NullPointerException{
        Preconditions.checkNotNull(role);
        resolve(role).ifPresent(column -> this.remove(LongType.SELFASSIGNABLE, column));
    }

    /**
     * Associates the specified group name with the role in the underlying table.
     * Note that we only allow for a role to be associated with at most one group.
     * @param column the specified group name
     * @param value the specified role
     * @throws NullPointerException if {@code column} or {@code value} is null
     * @throws IllegalArgumentException if the role is already in another group
     */
    public synchronized void add(@Nonnull String column, @Nonnull Role value) throws NullPointerException, IllegalArgumentException{
        Preconditions.checkNotNull(column);
        Preconditions.checkNotNull(value);

        if(resolve(value).isPresent())
            throw new IllegalArgumentException();
        else
            add(LongType.SELFASSIGNABLE, column, value.getIdLong());
    }

    /**
     * Removes the mapping for an entry in the underlying table.
     * @param column the specified group name
     * @param value the role associated with a group name
     * @throws NullPointerException if {@code column} or {@code value} is null
     */
    public synchronized void remove(@Nonnull String column, @Nonnull Role value) throws NullPointerException{
        Preconditions.checkNotNull(column);
        Preconditions.checkNotNull(value);
        remove(LongType.SELFASSIGNABLE, column, value.getIdLong());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   Subreddits                                                                                                   //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks if the text channel exists in the specified group.
     * @param column the group name the role is associated with
     * @param value the channel associated with the group name
     * @return true if there exists a channel in the specified group
     * @throws NullPointerException if {@code column} or {@code value} is null
     */
    public synchronized boolean resolve(@Nonnull String column, @Nonnull TextChannel value) throws NullPointerException{
        Preconditions.checkNotNull(column);
        Preconditions.checkNotNull(value);
        return resolve(LongType.SUBREDDIT, column, value.getIdLong());
    }

    /**
     * Checks if the specified {@code channel} is within a group and returns the name associated with it.
     * @param channel the specified channel
     * @return an {@link Optional} with the group name associated with the channel.
     *         If no such association exists, {@link Optional#empty()} is returned.
     * @throws NullPointerException {@code channel} is null
     */
    @Nonnull
    public synchronized Set<String> resolve(@Nonnull TextChannel channel) throws NullPointerException{
        Preconditions.checkNotNull(channel);
        return resolve(LongType.SUBREDDIT, channel.getIdLong());
    }

    /**
     * Removes the mapping for an entry in the underlying table.
     * @param channel the channel associated with a group name
     * @throws NullPointerException if {@code channel} is null
     */
    public synchronized void remove(@Nonnull TextChannel channel) throws NullPointerException{
        Preconditions.checkNotNull(channel);
        resolve(channel).forEach(column -> this.remove(LongType.SUBREDDIT, column));
    }

    /**
     * Associates the specified group name with the role in the underlying table.
     * @param column the specified group name
     * @param value the specified channel
     * @throws NullPointerException if {@code column} or {@code value} is null
     */
    public synchronized void add(@Nonnull String column, @Nonnull TextChannel value) throws NullPointerException{
        Preconditions.checkNotNull(column);
        Preconditions.checkNotNull(value);
        add(LongType.SUBREDDIT, column, value.getIdLong());
    }

    /**
     * Removes the mapping for an entry in the underlying table.
     * @param column the specified group name
     * @param value the specified channel
     * @throws NullPointerException if {@code column} or {@code value} is null
     */
    public synchronized void remove(@Nonnull String column, @Nonnull TextChannel value) throws NullPointerException{
        Preconditions.checkNotNull(column);
        Preconditions.checkNotNull(value);
        remove(LongType.SUBREDDIT, column, value.getIdLong());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //                                                                                                                //
    //   All groups                                                                                                   //
    //                                                                                                                //
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * If the configuration file doesn't have an entry for the specified {@code row}, an empty {@link Multimap}
     * is returned.
     * Note that the returned {@link Multimap} is not a view of the internal values, it simply is a representation
     * of the internal state at the time the {@link Multimap} was created.
     * @param row the row associated with its entries
     * @return an unmodifiable copy of all entries associated with the specified {@code row}
     * @throws NullPointerException if {@code row} is null
     */
    @Nonnull
    public synchronized Multimap<String, Long> resolve(@Nonnull LongType row) throws NullPointerException{
        Preconditions.checkNotNull(row);
        return Multimaps.unmodifiableMultimap(groups.computeIfAbsent(row, x -> LinkedHashMultimap.create()));
    }
    /**
     * Checks if the specified entry exists in the table.
     * @param row the type associated with the id
     * @param column name associated with the id
     * @param value the id associated with a type and name
     * @return true if there exists an entry at the specified
     *         {@code row} and {@code column} that matches the {@code value}
     * @throws NullPointerException if the {@code row} or {@code column} is null
     */
    public synchronized boolean resolve(@Nonnull LongType row, @Nonnull String column, long value) throws NullPointerException{
        Preconditions.checkNotNull(row);
        Preconditions.checkNotNull(column);
        return resolve(row).containsEntry(column, value);
    }

    /**
     * Checks if the specified {@code id} is within a group and returns all names associated with it.
     * @param id the specified id
     * @return an {@link Optional} with the group name associated with the id.
     *         If no such association exists, {@link Optional#empty()} is returned.
     * @throws NullPointerException {@code row} is null
     */
    @Nonnull
    public synchronized Set<String> resolve(@Nonnull LongType row, long id) throws NullPointerException{
        Preconditions.checkNotNull(row);
        //Get all keys that have 'id' as a value
        return Multimaps.filterValues(resolve(row), value -> Objects.equals(value, id)).keySet();
    }

    /**
     * Removes all values associated with the specified {@code row} and {@code column}.
     * This method is necessary when modifying the available subreddits in the {@link RedditFeed}, which needs
     * to be reflected in all guild configurations that share the associated subreddit.
     * @param row the specified row
     * @param column the specified column
     * @throws NullPointerException if {@code row} or {@code column} is null
     */
    public synchronized void remove(@Nonnull LongType row, @Nonnull String column) throws NullPointerException{
        Preconditions.checkNotNull(row);
        Preconditions.checkNotNull(column);

        if(groups.containsKey(row))
            groups.get(row).removeAll(column);
    }

    /**
     * Associates an id with the specified {@code row} and {@code value}.
     * This method is necessary when modifying a subreddit feed over a specific text channel
     * in the {@link RedditFeed}, which needs to be reflected in the respective guild configurations as well.
     * @param row the type associated with the id
     * @param column the group name associated with the id.
     * @param id the id associated with the type and group name
     * @throws NullPointerException if {@code row} or {@code column} is null
     */
    public synchronized void add(@Nonnull LongType row, @Nonnull String column, long id) throws NullPointerException{
        Preconditions.checkNotNull(row);
        Preconditions.checkNotNull(column);

        if(!groups.containsKey(row))
            groups.put(row, LinkedHashMultimap.create());
        groups.get(row).put(column, id);
    }

    /**
     * Removes the {@code value} associated with the specified {@code row} and {@code column}.
     * This method is necessary when modifying a subreddit feed over a specific text channel
     * in the {@link RedditFeed}, which needs to be reflected in the respective guild configurations as well.
     * @param row the type associated with the id
     * @param column the group name associated with the id.
     * @param value the value associated with the type and group name
     * @throws NullPointerException if {@code row} or {@code column} is null
     */
    public synchronized void remove(@Nonnull LongType row, @Nonnull String column, long value) throws NullPointerException{
        Preconditions.checkNotNull(row);
        Preconditions.checkNotNull(column);

        if(groups.containsKey(row))
            groups.get(row).remove(column, value);
    }

    /**
     * Transforms each id associated with the specified row into a new data type. All values for which the mapper
     * returns null are ignored. The returned multimap is not a life-view of the configuration.
     * So any changes in the configuration will not be reflected in hte multimap.<br>
     * The main purpose of this method is to recreate the text channels, roles or other types from their id.
     * @param row the type associated with the id
     * @param mapper a function for transforming the ids
     * @param <T> the data type the ids are transformed into
     * @return an immutable multimap of all key-value pairs
     * @throws NullPointerException if {@code row} or {@code mapper} is null
     */
    public synchronized <T> Multimap<String, T> resolve(@Nonnull LongType row, @Nonnull Function<Long, T> mapper) throws NullPointerException{
        if(!groups.containsKey(row))
            return Multimaps.unmodifiableMultimap(HashMultimap.create());
        Multimap<String, T> multimap = HashMultimap.create();
        for(String column : groups.get(row).keySet())
            multimap.putAll(column, resolve(row, column, mapper));
        return Multimaps.unmodifiableMultimap(HashMultimap.create());
    }


    /**
     * Transforms each id associated with the specified row and column into a new data type.
     * All values for which the mapper returns null are ignored.
     * The returned collection is not a life-view of the configuration. So any changes in the configuration will
     * not be reflected in the multimap.<br>
     * The main purpose of this method is to recreate the text channels, roles or other types from their id.
     * @param row the type associated with the id
     * @param column the group name associated with the id.
     * @param mapper a function for transforming the ids
     * @param <T> the data type the ids are transformed into
     * @return an immutable collection of all transformed entries
     * @throws NullPointerException if {@code row}, {@code column} or {@code mapper} is null
     */
    public synchronized <T> Collection<T> resolve(@Nonnull LongType row, @Nonnull String column, @Nonnull Function<Long, T> mapper) throws NullPointerException{
        if(!groups.containsKey(row))
            return Collections.emptyList();
        return groups.get(row).get(column).stream().map(mapper).filter(Objects::nonNull).collect(Collectors.toUnmodifiableList());
    }

    /**
     * A collection containing all valid row elements in the table.
     */
    public enum LongType{
        /**
         * This entry is used for all self-assignable roles.<br>
         * At each time, a user can only have one role in each group and each role can at most be in one such group.
         */
        SELFASSIGNABLE("selfassignable", (g, l) -> g.getRoleById(l) != null),
        /**
         * This entry is used for all reddit feeds.<br>
         * Every time a new submission is received, it is posted in all channels in this scope.
         */
        SUBREDDIT("subreddit", (g, l) -> g.getTextChannelById(l) != null);
        /**
         * The name of the row in the guild file.
         */
        @Nonnull
        private final String name;
        /**
         * A function that checks if the given id is still valid.<br>
         * An id can become invalid, if a role or text channel gets deleted, for example.
         */
        @Nonnull
        private final BiFunction<Guild, Long, Boolean> checker;

        /**
         * @param name the name of the row in the configuration file
         * @param checker the function checking if the entity associated with the id still exists
         * @throws NullPointerException if {@code name} or {@code checker} is null
         */
        LongType(@Nonnull String name, @Nonnull BiFunction<Guild, Long, Boolean> checker) throws NullPointerException{
            Preconditions.checkNotNull(name);
            Preconditions.checkNotNull(checker);
            this.name = name;
            this.checker = checker;
        }

        /**
         * @return the name identifying this row in the guild file
         */
        @Nonnull
        public String getName(){
            return name;
        }

        /**
         * Checks if the entity associated with the guild still exists. A role or text channel might've been deleted
         * since the last update. This method is intended to detect such outdated values.
         * @param guild the guild associated with this configuration file
         * @param id the id associated with an entity
         * @return true, if the entity still exists
         * @throws NullPointerException if {@code guild} is null
         */
        public boolean exists(@Nonnull Guild guild, long id) throws NullPointerException{
            Preconditions.checkNotNull(guild);
            return checker.apply(guild, id);
        }
    }

    /**
     * Applies the visitor pattern on all entries in this configuration.
     * @param visitor the specified visitor.
     */
    public void accept(Visitor visitor){
        visitor.handle(this);
    }

    public interface Visitor {
        default void visit(@Nonnull Configuration.LongType type, @Nonnull String key, @Nonnull Collection<Long> values){}
        default void traverse(@Nonnull Configuration.LongType type, @Nonnull String key, @Nonnull Collection<Long> values){}
        default void endVisit(@Nonnull Configuration.LongType type, @Nonnull String key, @Nonnull Collection<Long> values){}
        default void handle(@Nonnull Configuration.LongType type, @Nonnull String key, @Nonnull Collection<Long> values) throws NullPointerException{
            Preconditions.checkNotNull(type);
            Preconditions.checkNotNull(key);
            Preconditions.checkNotNull(values);
            visit(type, key, values);
            traverse(type, key, values);
            endVisit(type, key, values);
        }

        default void visit(@Nonnull Pattern pattern){}
        default void traverse(@Nonnull Pattern pattern){}
        default void endVisit(@Nonnull Pattern pattern){}
        default void handle(@Nonnull Pattern pattern) throws NullPointerException{
            Preconditions.checkNotNull(pattern);
            visit(pattern);
            traverse(pattern);
            endVisit(pattern);
        }

        default void visit(@Nonnull String prefix){}
        default void traverse(@Nonnull String prefix){}
        default void endVisit(@Nonnull String prefix){}
        default void handle(@Nonnull String prefix) throws NullPointerException{
            Preconditions.checkNotNull(prefix);
            visit(prefix);
            traverse(prefix);
            endVisit(prefix);
        }

        default void visit(@Nonnull Configuration configuration){}
        default void traverse(@Nonnull Configuration configuration) throws NullPointerException{
            Preconditions.checkNotNull(configuration);
            configuration.getPattern().ifPresent(this::handle);
            configuration.getPrefix().ifPresent(this::handle);
            configuration.groups.forEach((type, multimap) -> multimap.asMap().forEach((key, values) -> this.handle(type, key, values)));
        }
        default void endVisit(@Nonnull Configuration configuration){}
        default void handle(@Nonnull Configuration configuration) throws NullPointerException{
            Preconditions.checkNotNull(configuration);
            visit(configuration);
            traverse(configuration);
            endVisit(configuration);
        }
    }
}