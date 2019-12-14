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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import vartas.discord.bot.Command;
import vartas.discord.bot.CommandBuilder;
import vartas.discord.bot.entities.Configuration;
import vartas.discord.bot.entities.Shard;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * This listener is responsible for parsing the received messages and transforming them into commands,
 * whenever possible.
 */
@Nonnull
public class CommandListener extends ListenerAdapter {
    /**
     * The global prefix that is valid in all guilds and private messages.
     */
    @Nonnull
    private final String globalPrefix;
    /**
     * The log for this class
     */
    @Nonnull
    private final Logger log = JDALogger.getLog(this.getClass());
    /**
     * The shard associated with this listener.
     * It is required to schedule the created command.
     */
    @Nonnull
    private final Shard shard;
    /**
     * The builder for transforming the messages into commands.
     */
    @Nonnull
    private final CommandBuilder builder;

    /**
     * Initializes a fresh listener.
     * @param shard the shard associated with this listener
     * @param builder the command builder for the messages
     * @param globalPrefix the global prefix
     * @throws NullPointerException if {@code shard}, {@code builder} or {@code globalPrefix} is null
     */
    public CommandListener(@Nonnull Shard shard, @Nonnull CommandBuilder builder, @Nonnull String globalPrefix) throws NullPointerException{
        this.shard = shard;
        this.builder = builder;
        this.globalPrefix = globalPrefix;
    }

    /**
     * A message was received on either a private channel or a guild channel.
     * @param event the corresponding event.
     * @throws NullPointerException if {@code event} is null
     */
    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) throws NullPointerException{
        Preconditions.checkNotNull(event);
        User author = event.getAuthor();
        Message message = event.getMessage();

        //Ignore all bot messages
        if(author.isBot())
            return;
        //Return on success
        if(parseWithGlobalPrefix(message))
            return;
        //Check the guild prefix if inside a guild
        if (message.isFromGuild())
            parseWithGuildPrefix(message);
    }

    /**
     * Checks if the message starts with the global prefix. If so, the prefix-free message content is parsed.
     * @param message the received message
     * @return true if the message content starts with the global prefix
     * @throws NullPointerException if {@code message} is null
     */
    private boolean parseWithGlobalPrefix(@Nonnull Message message) throws NullPointerException{
        Preconditions.checkNotNull(message);
        String content = message.getContentRaw();

        if(content.startsWith(globalPrefix)){
            parse(StringUtils.removeStart(content, globalPrefix), message);
            return true;
        }else{
            return false;
        }
    }

    /**
     * Checks if the message starts with the guild prefix. If so, the prefix-free message content is parsed.
     * @param message the received message
     * @throws NullPointerException if {@code message} is null
     */
    private void parseWithGuildPrefix(@Nonnull Message message){
        Preconditions.checkNotNull(message);
        Guild guild = message.getGuild();
        Configuration configuration = shard.guild(guild);
        Optional<String> prefixOpt = configuration.getPrefix();
        String content = message.getContentRaw();

        if(prefixOpt.isPresent()){
            String prefix = prefixOpt.get();
            parse(StringUtils.removeStart(content, prefix), message);
        }
    }

    /**
     * Parses the prefix-free message content and schedules it, upon success.<br>
     * In case of an error, the error message is posted in the same channel the message was received in.
     * @param prefixFreeContent the prefix-free message content
     * @param message the received message
     * @throws NullPointerException if {@code prefixFreeContent} or {@code message} is null
     */
    private void parse(@Nonnull String prefixFreeContent, @Nonnull Message message) throws NullPointerException{
        Preconditions.checkNotNull(prefixFreeContent);
        Preconditions.checkNotNull(message);
        MessageChannel channel = message.getChannel();
        try {
            Command command = builder.build(prefixFreeContent, message);
            shard.schedule(new CommandWrapper(command, channel));
            log.info("Executed "+command.getClass().getSimpleName());
        }catch(RuntimeException e){
            e.printStackTrace();
            String errorMessage = e.toString();
            log.error(errorMessage);
            shard.queue(channel.sendMessage(errorMessage));
        }
    }


    /**
     * This class is wrapped around the parsed command and will catch any instance of {@link Throwable}.
     * The caught error message is sent to the channel, to notify the user that something went wrong,
     * then the error is rethrown.
     */
    @Nonnull
    private class CommandWrapper implements Runnable{
        /**
         * The parsed command.
         */
        @Nonnull
        private final Command command;
        /**
         * The channel the command is sent in.
         */
        @Nonnull
        private final MessageChannel channel;

        /**
         * Creates a fresh wrapper.
         * @param command the command
         * @param channel the target channel
         * @throws NullPointerException if {@code command} or {@code channel} is null
         */
        public CommandWrapper(@Nonnull Command command, @Nonnull MessageChannel channel) throws NullPointerException{
            this.command = command;
            this.channel = channel;
        }

        /**
         * Attempts to execute the command.
         * Upon failure, the error message is sent to the target channel instead, then the error is rethrown.
         */
        @Override
        public void run(){
            try{
                command.run();
            }catch(Throwable e){
                e.printStackTrace();
                String errorMessage = e.toString();
                shard.queue(channel.sendMessage(errorMessage));
                throw e;
            }
        }
    }
}
