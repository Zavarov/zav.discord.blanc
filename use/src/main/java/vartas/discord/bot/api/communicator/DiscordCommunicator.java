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
package vartas.discord.bot.api.communicator;

import de.monticore.symboltable.GlobalScope;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.jfree.chart.JFreeChart;
import vartas.discord.bot.api.environment.EnvironmentInterface;
import vartas.discord.bot.api.listener.MessageListener;
import vartas.discord.bot.api.message.InteractiveMessage;
import vartas.discord.bot.api.threads.ActivityTracker;
import vartas.discord.bot.api.threads.MessageTracker;
import vartas.discord.bot.exec.AbstractCommandBuilder;

import java.util.Collection;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * The concrete implementation of the communicator.
 */
public class DiscordCommunicator implements CommunicatorInterface {
    /**
     * The tracker that removes interactive message that are too old.
     */
    protected final MessageTracker messages;
    /**
     * The activity tracker for all message.
     */
    protected final ActivityTracker activity;
    /**
     * The environment of the program.
     */
    protected final EnvironmentInterface environment;
    /**
     * The message listener for the JDA.
     */
    protected final MessageListener listener;
    /**
     * The JDA that this communicator uses.
     */
    protected final JDA jda;
    /**
     * Initializes all necessary tasks for the communicator in this shard.
     * @param environment the environment of the program
     * @param jda the JDA that this communicator uses.
     * @param commands the scope for all valid commands
     * @param builder the builder for generating the commands from the calls
     */
    public DiscordCommunicator(EnvironmentInterface environment, JDA jda, GlobalScope commands, AbstractCommandBuilder builder){
        this.environment = environment;
        this.jda = jda;
        this.activity = new ActivityTracker(this);
        this.messages = new MessageTracker(this);
        this.listener = new MessageListener(this, messages, commands, builder);

        jda.addEventListener(listener);
    }
    /**
     * Sends a the interactive message in the specified channel.
     * @param message the interactive message that is sent.
     */
    @Override
    public void send(InteractiveMessage message){
        send(message.toRestAction(messages::add), message);
    }
    /**
     * Updates the activity tracker by a new message in the given channel.
     * @param channel the channel in which activity was observed.
     */
    @Override
    public void activity(TextChannel channel){
        activity.increase(channel);
    }
    /**
     * @param guild the guild the chart is plotted over.
     * @param channels the channels that are also plotted.
     * @return a chart over the activity in the guild and also the selected channels.
     */
    @Override
    public JFreeChart activity(Guild guild, Collection<TextChannel> channels){
        return activity.createChart(guild, channels);
    }
    /**
     * @return the underlying environment of the program. 
     */
    @Override
    public EnvironmentInterface environment(){
        return environment;
    }
    /**
     * @return the jda in the current shard. 
     */
    @Override
    public JDA jda(){
        return jda;
    }

    /**
     * Attempts to shutdown all ongoing tasks.
     * @return the result once all tasks have been finished.
     */
    @Override
    public Future<?> shutdown() {
        jda.shutdown();
        executor.shutdown();
        log.info("Shutting down shard "+jda.getShardInfo().getShardString()+".");
        return new FutureTask<>(() -> executor.awaitTermination(1, TimeUnit.MINUTES));
    }
}