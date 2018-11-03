/*
 * Copyright (C) 2017 u/Zavarov
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

package vartas.discordbot.command;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.JDALogger;
import org.slf4j.Logger;
import vartas.discordbot.DiscordBot;
import vartas.discordbot.messages.InteractiveMessage;
import vartas.discordbot.threads.ActivityTracker;
import vartas.discordbot.threads.MessageTracker;
import vartas.parser.ast.AbstractSyntaxTree;
import vartas.parser.cfg.ContextFreeGrammar.Token;
import vartas.parser.cfg.ContextFreeGrammar.Type;
import vartas.reddit.PushshiftWrapper;
import vartas.reddit.RedditBot;
import vartas.xml.XMLConfig;
import vartas.xml.XMLPermission;

/**
 * The frame for every command.
 * @author u/Zavarov
 */
public abstract class Command implements Runnable{
    /**
     * A set if all ranks that can always execute this command.
     */
    protected final Set<Rank> ranks = Sets.newHashSet(Rank.ROOT, Rank.USER);
    /**
     * The parameter of this command.
     */
    protected List<Token> parameter;
    /**
     * The message that triggered the command.
     */
    protected Message message;
    /**
     * The bot of the current shard.
     */
    protected DiscordBot bot;
    /**
     * The logger of the class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
    /**
     * A builder for the output message.
     */
    protected final StringBuilder builder = new StringBuilder();
    /**
     * The configuration file.
     */
    protected XMLConfig config;
    /**
     * The tracker for the activity in the guilds.
     */
    protected ActivityTracker activity;
    /**
     * The tracker for all interactive messages.
     */
    protected MessageTracker interactives;
    /**
     * The permission file
     */
    protected XMLPermission permission;
    /**
     * The instance that is responsible for communicating with the Reddit API.
     */
    protected RedditBot reddit;
    /**
     * The instance that contains all the data of the crawler.
     */
    protected PushshiftWrapper pushshift;
    /**
     * Sets the parameter of the command.
     * @param parameter the new parameter.
     */
    public void setParameter(List<Token> parameter){
        this.parameter = parameter;
    }
    /**
     * Sets the parameter of the command.
     * @param parameter the new parameter.
     */
    public void setParameter(AbstractSyntaxTree parameter){
        List<Token> list = new ObjectArrayList<>();
        parameter.forEach(list::add);
        setParameter(list);
    }
    /**
     * Sets the message that caused this command.
     * @param message the new message.
     */
    public final void setMessage(Message message){
        this.message = message;
    }
    /**
     * Sets the bot instance the command can use.
     * @param bot the new bot instance.
     */
    public final void setBot(DiscordBot bot){
        this.bot = bot;
    }
    /**
     * Sets the configuration file of the command.
     * @param config the new configuration file.
     */
    public final void setConfig(XMLConfig config){
        this.config = config;
    }
    /**
     * Sets the permission file of the command.
     * @param permission the new permission file.
     */
    public final void setPermission(XMLPermission permission){
        this.permission = permission;
    }
    /**
     * Sets activity tracker.
     * @param activity the activity tracker.
     */
    public final void setActivityTracker(ActivityTracker activity){
        this.activity = activity;
    }
    /**
     * Sets the message tracker.
     * @param interactives the message tracker.
     */
    public final void setMessageTracker(MessageTracker interactives){
        this.interactives = interactives;
    }
    /**
     * Sets the communicator with the Reddit API.
     * @param reddit the reddit client.
     */
    public final void setRedditBot(RedditBot reddit){
        this.reddit = reddit;
    }
    /**
     * Sets the communicator with the crawler.
     * @param pushshift the new communicator.
     */
    public final void setPushshiftWrapper(PushshiftWrapper pushshift){
        this.pushshift = pushshift;
    }
    
    /**
     * Extracts the command that was specified in the message.
     * @param name the class path.
     * @param message the message that triggered a command.
     * @param parameter the parameter of the command.
     * @param bot the bot that received the message.
     * @param config the configuration file.
     * @return the command that was called.
     * @throws ClassNotFoundException if no class is associated with the command.
     * @throws NoSuchMethodException if the constructor of the class is malformed.
     * @throws InstantiationException if the object couldn't be created.
     * @throws IllegalAccessException if the constructor isn't public.
     * @throws IllegalArgumentException if the parameter are invalid.
     * @throws InvocationTargetException if the class triggered an unspecified error.
     */
    public static Command createCommand(String name, Message message, AbstractSyntaxTree parameter, DiscordBot bot, XMLConfig config) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Command command = Class.forName(name).asSubclass(Command.class).newInstance();
        command.setMessage(message);
        command.setParameter(parameter);
        command.setBot(bot);
        command.setConfig(config);
        return command;
    }
    /**
     * Checks if the command was executed inside of a guild.
     * @throws CommandRequiresGuildException if the message wasn't from a guild.
     */
    public final void requiresGuild() throws CommandRequiresGuildException{
        if(message.getGuild() == null){
            throw new CommandRequiresGuildException();
        }
    }
    /**
     * Checks if the message that is connected to the command has any attachments.
     * @throws CommandRequiresAttachmentException if the message has no attachments.
     */
    public final void requiresAttachment() throws CommandRequiresAttachmentException{
        if(message.getAttachments().isEmpty()){
           throw new CommandRequiresAttachmentException(); 
        }
    }
    /**
     * Checks if the user has enough permissions to execute the command.
     * @throws MissingRankException when the user has insufficient rank to use the command
     * @throws MissingPermissionException when the user has insufficient permission to use the command
     */
    protected void checkRank() throws MissingRankException{
        //Ranks disjoint <=> no Rank in common
        if(Collections.disjoint(ranks, permission.getRanks(message.getAuthor())))
            throw new MissingRankException(ranks.iterator().next());
    }
    /**
     * Executes the command.
     */
    @Override
    public void run(){
        try{
            parameter = filter();
            checkRank();
            execute();
        //Expected errors
        }catch(CommandRequiresAttachmentException | CommandRequiresGuildException | UnknownEntityException | MissingRankException | MissingPermissionException e){
            DiscordBot.sendMessage(message.getChannel(), e.getMessage());
        }catch(RuntimeException | IOException | InterruptedException e){
            InteractiveMessage.Builder error = new InteractiveMessage.Builder(
                    message.getChannel(),
                    message.getAuthor()
            );
            error.addLine(e.toString());
            StackTraceElement[] stack_trace = e.getStackTrace();
            for(int i = 0 ; i < stack_trace.length ; i+=10){
              for(int j = i ; j < Math.min(i+10,stack_trace.length) ; ++j){
                  error.addLine(stack_trace[j].toString());
              }
              error.nextPage();
            }
            log.error(String.format("%s",e.toString()));
            error.build().send();
        }
        log.info(String.format("Command executed by %s in %s %s",
                message.getAuthor().getName(),
                "in "+message.getChannel().getName(),
                message.getGuild() == null ? "." : " in "+ message.getGuild().getName()+"."
        ));
    }
    /**
     * The method every command has to implement.
     * @throws IOException if it failed modifying configuration files.
     * @throws InterruptedException if the command was interrupted before it could complete.
     */
    protected abstract void execute() throws IOException, InterruptedException;
    /**
     * Filters all token that are nonterminals.
     * @return a list tokens that are relevant.
     */
    protected List<Token> filter(){
        return parameter.stream().filter(p -> p.getType() != Type.NONTERMINAL).collect(Collectors.toList());
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
     * @param objects a set of possible roles.
     * @return the role instances specified by the tokens.
     */
    protected final Set<Role> getRole(Iterable<Token> objects){
        return Command.getEntity(
                id -> message.getGuild().getRoleById(id),
                name -> message.getGuild().getRolesByName(name, true),
                objects
        );
    }
    /**
     * @param objects a set of possible users.
     * @return the user instances specified by the tokens or a set containing only the author of the message, if it is empty.
     */
    protected final Set<User> getDefaultUser(Collection<Token> objects){
        Set<User> user = getUser(objects);
        if(user.isEmpty()){
            user.add(message.getAuthor());
        }
        return user;
    }
    /**
     * @param objects a set of possible users.
     * @return the user instances specified by the tokens.
     */
    protected final Set<User> getUser(Collection<Token> objects){
        return Command.getEntity(id -> bot.getJda().retrieveUserById(id).complete(),
            name -> bot.getJda().getUsersByName(name, true),
            objects
        );
    }
    /**
     * @param objects a set of possible member.
     * @return the member instances specified by the tokens or a set containing only the author of the message, if it is empty.
     */
    protected final Set<Member> getDefaultMember(Iterable<Token> objects){
        Set<Member> member = getMember(objects);
        if(member.isEmpty() && message.getMember()!=null){
            member.add(message.getMember());
        }
        return member;
    }
    /**
     * @param objects a set of possible members.
     * @return the member instances specified by the tokens.
     */
    protected final Set<Member> getMember(Iterable<Token> objects){
        if(message.getGuild()!=null){
            return Command.getEntity(
                        message.getGuild()::getMemberById, 
                        name -> message.getGuild().getMembersByEffectiveName(name, true), 
                        objects
            );
        }else{
            return Sets.newHashSet();
        }
    }
    /**
     * @param objects a set of possible guilds.
     * @return the guild instances specified by the tokens or a set containing only the guild the message was executed in, if it is empty.
     */
    protected final Set<Guild> getDefaultGuild(Iterable<Token> objects){
        Set<Guild> guild = getGuild(objects);
        if(guild.isEmpty() && message.getGuild() != null){
            guild.add(message.getGuild());
        }
        return guild;
    }
    /**
     * @param objects a set of possible guilds.
     * @return the guild instances specified by the tokens.
     */
    protected final Set<Guild> getGuild(Iterable<Token> objects){
        return Command.getEntity(
                id->bot.getJda().getGuildById(id), 
                name -> bot.getJda().getGuildsByName(name, true),
                objects
        );
            
    }
    /**
     * @param objects a set of possible textchannels.
     * @return the textchannel instances specified by the tokens or a set containing only the textchannel the message was executed in, if it is empty.
     */
    protected final Set<TextChannel> getDefaultTextChannel(Iterable<Token> objects){
        Set<TextChannel> channel = getTextChannel(objects);
        if(channel.isEmpty() && message.getGuild() != null){
            channel.add(message.getTextChannel());
        }
        return channel;
    }
    /**
     * @param objects a set of possible textchannels.
     * @return the textchannel instances specified by the tokens.
     */
    protected final Set<TextChannel> getTextChannel(Iterable<Token> objects){
        return Command.getEntity(
                id->message.getGuild().getTextChannelById(id),
                name->message.getGuild().getTextChannelsByName(name, true),
                objects
        );
    }
}