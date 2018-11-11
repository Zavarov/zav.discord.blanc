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
package vartas.discordbot.comm;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.File;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.SelfUser;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.Presence;
import net.dv8tion.jda.core.utils.JDALogger;
import org.jfree.chart.JFreeChart;
import org.slf4j.Logger;
import vartas.discordbot.MessageListener;
import vartas.discordbot.command.AmbiguousNameException;
import vartas.discordbot.command.UnknownEntityException;
import vartas.discordbot.messages.InteractiveMessage;
import vartas.discordbot.threads.ActivityTracker;
import vartas.discordbot.threads.MessageTracker;
import vartas.parser.cfg.ContextFreeGrammar.Token;
import vartas.xml.XMLServer;

/**
 * A frame of the communicator
 * @author u/Zavarov
 */
public abstract class AbstractCommunicator implements Communicator{
    /**
     * The logger for all changes.
     */
    protected final Logger log = JDALogger.getLog(this.getClass());
    /**
     * A map of all guilds and their respective server files.
     */
    protected final Map<Guild, XMLServer> servers = Maps.newHashMap();
    /**
     * The executor that processes all asynchronous processes.
     */
    protected final ExecutorService executor;
    /**
     * The tracker that removes interactive messages that are too old.
     */
    protected final MessageTracker messages;
    /**
     * The activity tracker for all messages.
     */
    protected final ActivityTracker activity;
    /**
     * The environment of the program.
     */
    protected final Environment environment;
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
     */
    protected AbstractCommunicator(Environment environment, JDA jda){
        this.environment = environment;
        this.jda = jda;
        this.activity = new ActivityTracker(this);
        this.messages = new MessageTracker(this);
        this.listener = new MessageListener(this);
        this.executor = Executors.newWorkStealingPool();
        
        jda.addEventListener(listener);
    }
    /**
     * Sends a the interactive message in the specified channel.
     * @param message the interactive message that is sent.
     */
    @Override
    public void send(InteractiveMessage message){
        send(message.toRestAction(messages::add));
    }
    /**
     * @param objects a set of tokens that identify text channels.
     * @param message the message that acts as a reference point.
     * @return the textchannel specified by the data or the current channel.
     */
    @Override
    public Set<TextChannel> defaultTextChannel(Iterable<Token> objects, Message message){
        Set<TextChannel> channel = textChannel(objects, message);
        if(channel.isEmpty() && message.getGuild() != null){
            channel.add(message.getTextChannel());
        }
        return channel;
    }
    /**
     * @param objects a set of tokens that identify text channels.
     * @param message the message that acts as a reference point.
     * @return the textchannel specified by the data.
     */
    @Override
    public Set<TextChannel> textChannel(Iterable<Token> objects, Message message){
        if(message.getChannelType() != ChannelType.TEXT)
            return Sets.newHashSet();
        
        return getEntity(
                id->message.getGuild().getTextChannelById(id),
                name->message.getGuild().getTextChannelsByName(name, true),
                objects
        );
    }
    /**
     * @param objects a set of tokens that identify guilds.
     * @param message the message that acts as a reference point.
     * @return the guilds specified by the data or the current guild.
     */
    @Override
    public Set<Guild> defaultGuild(Iterable<Token> objects, Message message){
        Set<Guild> guild = guild(objects, message);
        if(guild.isEmpty() && message.getGuild() != null){
            guild.add(message.getGuild());
        }
        return guild;
    }
    /**
     * @param objects a set of tokens that identify guilds.
     * @param message the message that acts as a reference point.
     * @return the guilds specified by the data.
     */
    @Override
    public Set<Guild> guild(Iterable<Token> objects, Message message){
        if(message.getGuild() == null)
            return Sets.newHashSet();
        
        return getEntity(
                id-> message.getJDA().getGuildById(id), 
                name -> message.getJDA().getGuildsByName(name, true),
                objects
        );
    }
    /**
     * @param objects a set of tokens that identify members.
     * @param message the message that acts as a reference point.
     * @return the members specified by the data or the current member.
     */
    @Override
    public Set<Member> defaultMember(Iterable<Token> objects, Message message){
        Set<Member> member = member(objects, message);
        if(member.isEmpty() && message.getMember()!=null){
            member.add(message.getMember());
        }
        return member;
    }
    /**
     * @param objects a set of tokens that identify members.
     * @param message the message that acts as a reference point.
     * @return the members specified by the data.
     */
    @Override
    public Set<Member> member(Iterable<Token> objects, Message message){
        if(message.getGuild()==null)
            return Sets.newHashSet();
            
        return getEntity(
                message.getGuild()::getMemberById, 
                name -> message.getGuild().getMembersByName(name, true), 
                objects
        );
    }
    /**
     * @param objects a set of tokens that identify users.
     * @param message the message that acts as a reference point.
     * @return the users specified by the data or the author of the message.
     */
    @Override
    public Set<User> defaultUser(Iterable<Token> objects, Message message){
        Set<User> user = user(objects, message);
        if(user.isEmpty()){
            user.add(message.getAuthor());
        }
        return user;
    }
    /**
     * @param objects a set of tokens that identify users.
     * @param message the message that acts as a reference point.
     * @return the users specified by the data.
     */
    @Override
    public Set<User> user(Iterable<Token> objects, Message message){
        return getEntity(
            message.getJDA()::getUserById,
            name -> message.getJDA().getUsersByName(name, true),
            objects
        );
    }
    /**
     * @param objects a set of tokens that identify roles.
     * @param message the message that acts as a reference point.
     * @return the roles specified by the data.
     */
    @Override
    public Set<Role> role(Iterable<Token> objects, Message message){
        return getEntity(
                message.getGuild()::getRoleById,
                name -> message.getGuild().getRolesByName(name, true),
                objects
        );
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
     * @param guild the guild we want the server file from.
     * @return the server file that is connected to the guild. 
     */
    @Override
    public XMLServer server(Guild guild){
        if(servers.containsKey(guild)){
            return servers.get(guild);
        }else{
            File file = new File(String.format("%s/guilds/%s.server",environment.config().getDataFolder(),guild.getId()));
            XMLServer server = file.exists() ? XMLServer.create(file) : new XMLServer();
            servers.put(guild, server);
            return server;
        }
    }
    /**
     * @return all guilds that in the current shard. 
     */
    @Override
    public Collection<Guild> guild(){
        return jda.getGuilds();
    }
    /**
     * Terminates all threads.
     */
    @Override
    public void shutdown(){
        messages.shutdown();
        activity.shutdown();
        executor.shutdownNow();
        jda.shutdown();
    }
    /**
     * Submits a runnable to be executed and some unspecific point in time.
     * @param runnable the runnable that is going to be executed.
     */
    @Override
    public void submit(Runnable runnable){
        executor.execute(runnable);
    }
    /**
     * A framework to retreive Discord instances from a set of token.
     * @param <Q> the type of the instances.
     * @param byId a function that retrieves the instances by its id.
     * @param byName a function that retrieves the instances by its name.
     * @param objects the token.
     * @return a set of the entities.
     */
    private static <Q> Set<Q> getEntity(Function<String,Q> byId, Function<String,List<Q>> byName, Iterable<Token> objects){
        Set<Q> result = new ObjectLinkedOpenHashSet<>();
        objects.forEach(entry-> 
            {
                if(entry.getValue().equals("integer")){
                        Q entity = byId.apply(entry.getKey());
                        if(entity != null){
                            result.add(byId.apply(entry.getKey()));
                        }else{
                            throw new UnknownEntityException(entry);
                        }
                }else{
                        List<Q> list = byName.apply(entry.getKey());
                        if(list.size()==1){
                            result.add(list.get(0));
                        }else if(list.size()>1){
                            throw new AmbiguousNameException(entry);
                        }else{
                            throw new UnknownEntityException(entry);
                        }
                }
            });
        return result;
    }
    /**
     * @return the underlying environment of the program. 
     */
    @Override
    public Environment environment(){
        return environment;
    }
    /**
     * @return the instance of the bot itself in the current shard.
     */
    @Override
    public SelfUser self(){
        return jda.getSelfUser();
    }
    /**
     * @return the jda in the current shard. 
     */
    @Override
    public JDA jda(){
        return jda;
    }
    /**
     * @return the presence of this program in the current shard. 
     */
    @Override
    public Presence presence(){
        return jda.getPresence();
    }
}