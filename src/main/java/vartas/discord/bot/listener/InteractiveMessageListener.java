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

package vartas.discord.bot.listener;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discord.bot.entities.Credentials;
import vartas.discord.bot.message.InteractiveMessage;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * A runnable that removes interactive message, once the user hasn't used them
 * after a certain amount of time.
 */
@Nonnull
public class InteractiveMessageListener extends ListenerAdapter {
    /**
     * The cache for all stored messages.
     */
    @Nonnull
    private final Cache<String, InteractiveMessage> cache;

    /**
     * Initializes an empty tracker
     * @param config the configuration file containing the lifetime of the messages.
     * @throws NullPointerException if {@code config} is null
     */
    public InteractiveMessageListener(@Nonnull Credentials config) throws NullPointerException{
        Preconditions.checkNotNull(config);
        cache = CacheBuilder
                .newBuilder()
                .expireAfterAccess(config.getInteractiveMessageLifetime(), TimeUnit.MINUTES)
                .build();

        Logger log = JDALogger.getLog(this.getClass());
        log.info("Message Tracker started.");
    }

    /**
     * Adds the given message to the cache.
     * @param message the corresponding instance.
     * @throws NullPointerException if {@code source} or {@code message} is null
     */
    public void add(@Nonnull Message source, @Nonnull InteractiveMessage message) throws NullPointerException{
        Preconditions.checkNotNull(source);
        Preconditions.checkNotNull(message);
        cache.put(source.getId(), message);
    }
    /**
     * An reaction was added to a message.
     * @param event the corresponding event.
     * @throws NullPointerException if {@code event} is null
     */
    @Override
    public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) throws NullPointerException{
        Preconditions.checkNotNull(event);
        if(Optional.ofNullable(event.getUser()).map(User::isBot).orElse(true))
            return;

        Optional.ofNullable(cache.getIfPresent(event.getMessageId())).ifPresent(message -> {
            if(event.getChannelType() == ChannelType.TEXT)
                message.update(event.getUser(), event.getTextChannel(), event.getMessageId(), event.getReactionEmote().getName());
            else
                message.update(event.getUser(), event.getChannel(), event.getMessageId(), event.getReactionEmote().getName());
        });
    }

    public void accept(InteractiveMessageListener.Visitor visitor){
        visitor.handle(this);
    }

    public interface Visitor {
        default void visit(@Nonnull InteractiveMessageListener interactiveMessageListener){}

        default void traverse(@Nonnull InteractiveMessageListener interactiveMessageListener) {}

        default void endVisit(@Nonnull InteractiveMessageListener interactiveMessageListener){}

        default void handle(@Nonnull InteractiveMessageListener interactiveMessageListener) throws NullPointerException{
            Preconditions.checkNotNull(interactiveMessageListener);
            visit(interactiveMessageListener);
            traverse(interactiveMessageListener);
            endVisit(interactiveMessageListener);
        }
    }
}