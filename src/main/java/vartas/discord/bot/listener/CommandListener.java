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

import com.google.common.collect.Maps;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.utils.JDALogger;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import vartas.discord.bot.Command;
import vartas.discord.bot.CommandBuilder;
import vartas.discord.bot.entities.DiscordCommunicator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CommandListener extends ListenerAdapter {
    protected Map<Guild, String> prefixes = Maps.newConcurrentMap();
    protected String prefix;
    /**
     * The log for this nested class.
     */
    protected Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * The communicator for the local shard.
     */
    protected DiscordCommunicator communicator;
    /**
     * Transforms the messages into executable commands.
     */
    protected CommandBuilder builder;

    /**
     * @param communicator the communicator of the local shard.
     * @param builder the command transformer for the messages.
     */
    public CommandListener(DiscordCommunicator communicator, CommandBuilder builder){
        this.communicator = communicator;
        this.builder = builder;
        this.prefix = communicator.environment().config().getGlobalPrefix();
    }

    public void set(Guild guild, String prefix){
        prefixes.put(guild, prefix);
    }

    public void remove(Guild guild){
        prefixes.remove(guild);
    }

    /**
     * A message was received on either a private channel or a guild channel.
     * @param event the corresponding event.
     */
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event){
        if(event.getAuthor().isBot())
            return;

        Message message = event.getMessage();

        if(hasPrefix(message)){
            try {
                Command command = builder.build(getContent(message), message);

                //Wrap the command in a runnable that notifies us about any uncaught exception.
                Runnable runnable = () -> {
                    try{
                        command.run();
                    }catch(RuntimeException e){
                        e.printStackTrace();
                        String error = e.getClass().getSimpleName() + ": " + e.getMessage();
                        JDALogger.getLog(command.getClass().getSimpleName()).error(error);
                        communicator.send(message.getChannel(), error);
                    }
                };

                communicator.schedule(runnable);
                log.info("Executed "+command.getClass().getSimpleName());
            }catch(RuntimeException e){
                e.printStackTrace();
                String error = e.getClass().getSimpleName() + ": " + e.getMessage();
                log.error(error);
                communicator.send(event.getMessage().getChannel(), error);
            }
        }
    }
    /**
     * @param message the input message
     * @return all valid prefixes for the message.
     */
    private List<String> getPrefixes(Message message){
        List<String> prefixes = new ArrayList<>(2);

        prefixes.add(prefix);

        if(message.isFromGuild())
            Optional.ofNullable(this.prefixes.getOrDefault(message.getGuild(), null)).ifPresent(prefixes::add);

        return prefixes;
    }
    /**
     * @param message the input message.
     * @return the raw content of the message without a prefix.
     */
    private String getContent(Message message){
        String content = message.getContentRaw();
        String prefix = getPrefixes(message).stream().filter(content::startsWith).findFirst().orElse("");
        return StringUtils.removeStart(content, prefix);
    }
    /**
     * @param message the input message.
     * @return true if the message starts with a valid prefix
     */
    private boolean hasPrefix(Message message){
        return getPrefixes(message).stream().anyMatch(message.getContentRaw()::startsWith);
    }
}
