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

package vartas.discordbot;

import com.google.common.collect.Table;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.core.entities.Message;
import vartas.automaton.Preprocessor;
import vartas.automaton.RegularExpression;
import vartas.automaton.Tokenizer.Token;
import vartas.discordbot.comm.Communicator;
import vartas.discordbot.command.Command;
import vartas.discordbot.command.ErrorCommand;
import vartas.parser.DeterministicTopDownParser;
import vartas.parser.UnexpectedTokenException;
import vartas.parser.ast.AbstractSyntaxTree;
import vartas.parser.cfg.ContextFreeGrammar;
import vartas.parser.cfg.ContextFreeGrammar.Production;
import vartas.parser.cfg.ContextFreeGrammar.Type;


/**
 * A modified version of a parser that can also associate a command with an input.
 * @author u/Zavarov
 */
public class CommandParser extends DeterministicTopDownParser{
    /**
     * The communicator of the program.
     */
    protected final Communicator comm;
    /**
     * @param preprocessor the preprocessor of the input tokens.
     * @param table the table of the parser.
     * @param comm the communicator of the program.
     */
    protected CommandParser(Preprocessor preprocessor, Table<String, ContextFreeGrammar.Token, Production> table, Communicator comm){
        super(comm.environment().grammar(), preprocessor, table);
        this.comm = comm;
    }
    /**
     * @param tree a subtree of the parsed input message.
     * @return the name of the class file of the command
     */
    private String getCommand(AbstractSyntaxTree tree){
        List<Token> command_nodes = new ObjectArrayList<>();
        tree.forEach(node -> {
            if(node.getType().equals(Type.TERMINAL)){
                command_nodes.add(node);
            }
        });
        
        return comm.environment().command().getCommand(command_nodes.stream().map(Token::getRight).reduce((i,j)->i+j).get());
    }
    /**
     * Extracts the command that was specified in the message.
     * @param message the message that triggered a command.
     * @param prefixfree the message without the prefix
     * @return the command that was called.
     */
    public Command parseCommand(Message message, String prefixfree){
        try{
            //Parse the content
            AbstractSyntaxTree tree = parse(prefixfree);
            //Split the command into its segments
            AbstractSyntaxTree data_tree = tree.firstChildSplit(comm.environment().config().getDataIdentifier());
            AbstractSyntaxTree command_tree = tree.firstSplit(comm.environment().config().getCommandIdentifier());
        
            String command = getCommand(command_tree);
            return Command.createCommand(command, message, data_tree, comm);
        }catch(ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
            throw new IllegalArgumentException(String.format("No corresponding command found for %s",prefixfree));
        }catch(UnexpectedTokenException token){
            Command command = new ErrorCommand(token);
            command.setCommunicator(comm);
            command.setMessage(message);
            command.setParameter(Arrays.asList());
            return command;
        }
    }
    /**
     * A modification of the builder class for this parser.
     */
    public static class Builder extends DeterministicTopDownParser.Builder{
        /**
         * The identifier for a random roll.
         */
        public static final String RANDOM = "random";
        /**
         * The identifier for a date.
         */
        public static final String DATE = "date";
        /**
         * The identifier for the online statuses.
         */
        public static final String ONLINESTATUS = "onlinestatus";
        /**
         * The identifier for the interval in the Reddit plots.
         */
        public static final String INTERVAL = "interval";
        /**
         * The communicator of the program.
         */
        protected final Communicator comm;
        /**
         * @param comm the communicator of the program.
         */
        public Builder(Communicator comm) {
            super(comm.environment().grammar());
            this.comm = comm;
        }
        /**
         * Creates a lexer that ignores any form of spaces.
         * @return the lexer for the token.
         */
        @Override
        public Preprocessor.Builder createLexer(){
            Preprocessor.Builder builder = super.createLexer();
            builder.addExpression(createRandom(), RANDOM);
            builder.addExpression(createDate(), DATE);
            builder.addExpression(createOnlinestatus(), ONLINESTATUS);
            builder.addExpression(createInterval(), INTERVAL);
            builder.addNumberSeparator(new Token("+","\\+"));
            builder.addNumberSeparator(new Token("-","-"));
            builder.addNumberSeparator(new Token("*","\\*"));
            builder.addNumberSeparator(new Token("/","/"));
            builder.addNumberSeparator(new Token("^","^"));
            builder.addNumberSeparator(new Token("%","%"));
            return builder;
        }
        /**
         * @return a regular expression for the different intervals of the Reddit plots
         */
        private RegularExpression createInterval(){
            RegularExpression.Parser parser = new RegularExpression.Parser();
            return parser.parse("day+week+month+year");
        }
        /**
         * @return a regular expression for a random number of the form xdy
         */
        private RegularExpression createOnlinestatus(){
            RegularExpression.Parser parser = new RegularExpression.Parser();
            return parser.parse("dnd+idle+online+invisible");
        }
        /**
         * @return a regular expression for a random number of the form xdy
         */
        private RegularExpression createRandom(){
            RegularExpression.Parser parser = new RegularExpression.Parser();
            RegularExpression number = parser.parse("(1+2+3+4+5+6+7+8+9)(0+1+2+3+4+5+6+7+8+9)*");
            RegularExpression d = RegularExpression.singleton('d');
            return RegularExpression.concatenation(number,d,number);
        }
        /**
         * @return a regular expression for a date of the form d-d-d where
         * d is an integer of arbitrary length, for the day, month and year each.
         */
        private RegularExpression createDate(){
            RegularExpression.Parser parser = new RegularExpression.Parser();
            RegularExpression day = parser.parse("(0+1+2+3+4+5+6+7+8+9)(0+1+2+3+4+5+6+7+8+9)");
            RegularExpression month = parser.parse("(0+1+2+3+4+5+6+7+8+9)(0+1+2+3+4+5+6+7+8+9)");
            RegularExpression year = parser.parse("(0+1+2+3+4+5+6+7+8+9)(0+1+2+3+4+5+6+7+8+9)(0+1+2+3+4+5+6+7+8+9)(0+1+2+3+4+5+6+7+8+9)");
            RegularExpression separator = RegularExpression.singleton('-');
            return RegularExpression.concatenation(day,separator,month,separator,year);
        }
        /**
         * @return a new instance of the parser. 
         */
        @Override
        public CommandParser build(){
            super.findConflicts();
            return new CommandParser(createLexer().build(), createTable(), comm);
        }
    }
}