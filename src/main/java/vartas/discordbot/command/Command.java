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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.utils.JDALogger;
import org.slf4j.Logger;
import vartas.automaton.Preprocessor;
import vartas.discordbot.CommandParser;
import vartas.discordbot.comm.Communicator;
import vartas.parser.Parser;
import vartas.parser.ast.AbstractSyntaxTree;
import vartas.parser.cfg.ContextFreeGrammar.Token;
import vartas.parser.cfg.ContextFreeGrammar.Type;

/**
 * The frame for every command.
 * @author u/Zavarov
 */
public abstract class Command implements Runnable{
    protected static final Set<String> ALLOWED_VALUES = Sets.newHashSet(CommandParser.Builder.DATE,
            CommandParser.Builder.ONLINESTATUS,
            CommandParser.Builder.INTERVAL,
            Preprocessor.STRING,
            Parser.Builder.INTEGER);
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
     * The communicator of the program.
     */
    protected Communicator comm;
    /**
     * The logger of the class.
     */
    protected final Logger log = JDALogger.getLog(this.getClass().getSimpleName());
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
     * Sets the communicator of the program.
     * @param comm the new communicator.
     */
    public final void setCommunicator(Communicator comm){
        this.comm = comm;
    }
    
    /**
     * Extracts the command that was specified in the message.
     * @param name the class path.
     * @param message the message that triggered a command.
     * @param parameter the parameter of the command.
     * @param comm the communicator of the program.
     * @return the command that was called.
     * @throws ClassNotFoundException if no class is associated with the command.
     * @throws NoSuchMethodException if the constructor of the class is malformed.
     * @throws InstantiationException if the object couldn't be created.
     * @throws IllegalAccessException if the constructor isn't public.
     * @throws IllegalArgumentException if the parameter are invalid.
     * @throws InvocationTargetException if the class triggered an unspecified error.
     */
    public static Command createCommand(String name, Message message, AbstractSyntaxTree parameter, Communicator comm) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException{
        Command command = Class.forName(name).asSubclass(Command.class).newInstance();
        command.setMessage(message);
        command.setParameter(parameter);
        command.setCommunicator(comm);
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
        if(Collections.disjoint(ranks, comm.environment().permission().getRanks(message.getAuthor())))
            throw new MissingRankException(
                    ranks.stream()
                            .sorted((u,v) -> v.compareTo(u))
                            .findFirst().get());
    }
    /**
     * Executes the command.
     */
    @Override
    public void run(){
        try{
            checkRequirements();
            checkRank();
            parameter = filter();
            execute();
        //Expected errors
        }catch(CommandRequiresAttachmentException | CommandRequiresGuildException | UnknownEntityException | MissingRankException | MissingPermissionException e){
            comm.send(message.getChannel(), e.getMessage());
        //Unexpected errors
        }catch(RuntimeException | IOException | InterruptedException e){
            ErrorCommand error = new ErrorCommand(e);
            error.setCommunicator(comm);
            error.setMessage(message);
            error.setParameter(parameter);
            error.execute();
        }
        log.info(String.format("Command executed by %s in %s %s",
                message.getAuthor().getName(),
                message.getChannel().getName(),
                message.getGuild() == null ? "." : " in "+ message.getGuild().getName()+"."
        ));
    }
    /**
     * Checks if all requirements like guilds and attachments are fulfilled.
     */
    protected void checkRequirements(){}
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
        return parameter.stream()
                .filter(p -> p.getType() == Type.TERMINAL)
                .filter(p -> ALLOWED_VALUES.contains(p.getValue()))
                .collect(Collectors.toList());
    }
}