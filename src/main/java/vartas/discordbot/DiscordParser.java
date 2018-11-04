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
import java.util.List;
import net.dv8tion.jda.core.entities.Message;
import vartas.automaton.Preprocessor;
import vartas.automaton.RegularExpression;
import vartas.automaton.Tokenizer.Token;
import vartas.discordbot.command.Command;
import vartas.parser.DeterministicTopDownParser;
import vartas.parser.ast.AbstractSyntaxTree;
import vartas.parser.cfg.ContextFreeGrammar;
import vartas.parser.cfg.ContextFreeGrammar.Production;
import vartas.parser.cfg.ContextFreeGrammar.Type;
import vartas.xml.XMLCommand;
import vartas.xml.XMLConfig;


/**
 * A modified version of a parser that can also associate a command with an input.
 * @author u/Zavarov
 */
public class DiscordParser extends DeterministicTopDownParser{
    /**
     * A set of all commands.
     */
    protected final XMLCommand commands;
    /**
     * The configuration file.
     */
    protected final XMLConfig config;
    /**
     * The prefix of the commands.
     */
    protected String prefix;
    /**
     * @param grammar the CFG of the valid commands.
     * @param commands a list of all commands.
     * @param preprocessor the preprocessor of the input tokens.
     * @param table the table of the parser.
     * @param config the configuration file.
     */
    protected DiscordParser(ContextFreeGrammar grammar, XMLCommand commands, Preprocessor preprocessor, Table<String, ContextFreeGrammar.Token, Production> table, XMLConfig config){
        super(grammar, preprocessor, table);
        this.commands = commands;
        this.config = config;
        this.prefix = config.getPrefix();
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
        return commands.getCommand(command_nodes.stream().map(Token::getRight).reduce((i,j)->i+j).get());
    }
    /**
     * Extracts the command that was specified in the message.
     * @param message the message that triggered a command.
     * @param bot the bot that received the message.
     * @param content the message without the prefix
     * @return the command that was called.
     */
    public Command parseCommand(Message message, DiscordBot bot, String content){
        try{
            //Parse the content
            AbstractSyntaxTree tree = parse(content);
            //Split the command into its segments
            AbstractSyntaxTree data_tree = tree.firstChildSplit(config.getDataIdentifier());
            AbstractSyntaxTree command_tree = tree.firstSplit(config.getCommandIdentifier());
        
            String command = getCommand(command_tree);
            return Command.createCommand(command, message, data_tree, bot ,config);
        }catch(ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
            throw new IllegalArgumentException(String.format("No corresponding class found for %s",content));
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
         * The list of all valid commands.
         */
        protected final XMLCommand commands;
        /**
         * The configuration file.
         */
        protected final XMLConfig config;
        /**
         * @param grammar the CFG for all commands.
         * @param commands the list of all commands.
         * @param config the configuration file.
         */
        public Builder(ContextFreeGrammar grammar, XMLCommand commands, XMLConfig config) {
            super(grammar);
            this.commands = commands;
            this.config = config;
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
            builder.addNumberSeparator(new Token("+","\\+"));
            builder.addNumberSeparator(new Token("-","-"));
            builder.addNumberSeparator(new Token("*","\\*"));
            builder.addNumberSeparator(new Token("/","/"));
            builder.addNumberSeparator(new Token("^","^"));
            builder.addNumberSeparator(new Token("%","%"));
            return builder;
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
        public DiscordParser build(){
            super.findConflicts();
            return new DiscordParser(grammar, commands, createLexer().build(), createTable(), config);
        }
    }
}