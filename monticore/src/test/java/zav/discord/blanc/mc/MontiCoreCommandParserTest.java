/*
 * Copyright (c) 2020 Zavarov
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

package zav.discord.blanc.mc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.command.parser.Argument;
import zav.discord.blanc.command.parser.IntermediateCommand;
import zav.discord.blanc.command.parser.Parser;
import zav.discord.blanc.databind.Message;
import zav.discord.blanc.mc.argument._ast.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class MontiCoreCommandParserTest {
    Parser parser;
    Message message;

    @BeforeEach
    public void setUp(){
        parser = new MontiCoreCommandParser();
        message = new Message();
    }

    @Test
    public void testParseEmpty(){
        assertThat(parser.parse(message)).isNull();
    }

    @Test
    public void testParseName(){
        message.setContent("b: command Argument");
        IntermediateCommand command = parser.parse(message);

        assertThat(command.getArguments()).hasSize(1);
        assertThat(command.getArguments().get(0)).isInstanceOf(ASTExpressionArgument.class);
        assertThat(command.getName()).isEqualTo("command");
        assertThat(command.getPrefix()).contains("b");
    
        Argument argument = command.getArguments().get(0);
        assertThat(argument.asString()).contains("Argument");
    }

    @Test
    public void testParseString(){
        message.setContent("b: command \"12345\"");
        IntermediateCommand command = parser.parse(message);

        assertThat(command.getArguments()).hasSize(1);
        assertThat(command.getArguments().get(0)).isInstanceOf(ASTStringArgument.class);
        assertThat(command.getName()).isEqualTo("command");
        assertThat(command.getPrefix()).contains("b");
    
        Argument argument = command.getArguments().get(0);
        assertThat(argument.asString()).contains("12345");
    }

    @Test
    public void testParseRole(){
        message.setContent("b: command <@&12345>");
        IntermediateCommand command = parser.parse(message);

        assertThat(command.getArguments()).hasSize(1);
        assertThat(command.getArguments().get(0)).isInstanceOf(ASTRoleArgument.class);
        assertThat(command.getName()).isEqualTo("command");
        assertThat(command.getPrefix()).contains("b");
    
        Argument argument = command.getArguments().get(0);
        assertThat(argument.asNumber().map(BigDecimal::longValue)).contains(12345L);
    }

    @Test
    public void testParseTextChannel(){
        message.setContent("b: command <#12345>");
        IntermediateCommand command = parser.parse(message);

        assertThat(command.getArguments()).hasSize(1);
        assertThat(command.getArguments().get(0)).isInstanceOf(ASTTextChannelArgument.class);
        assertThat(command.getName()).isEqualTo("command");
        assertThat(command.getPrefix()).contains("b");
    
        Argument argument = command.getArguments().get(0);
        assertThat(argument.asNumber().map(BigDecimal::longValue)).contains(12345L);
    }

    @Test
    public void testParseUser(){
        message.setContent("b: command <@12345>");
        IntermediateCommand command = parser.parse(message);

        assertThat(command.getArguments()).hasSize(1);
        assertThat(command.getArguments().get(0)).isInstanceOf(ASTUserArgument.class);
        assertThat(command.getName()).isEqualTo("command");
        assertThat(command.getPrefix()).contains("b");
    
        Argument argument = command.getArguments().get(0);
        assertThat(argument.asNumber().map(BigDecimal::longValue)).contains(12345L);
    }

    @Test
    public void testParseExpression(){
        message.setContent("b: command 5+3");
        IntermediateCommand command = parser.parse(message);

        assertThat(command.getArguments()).hasSize(1);
        assertThat(command.getArguments().get(0)).isInstanceOf(ASTExpressionArgument.class);
        assertThat(command.getName()).isEqualTo("command");
        assertThat(command.getPrefix()).contains("b");

        Argument argument = command.getArguments().get(0);
        assertThat(argument.asNumber().map(BigDecimal::intValue)).contains(8);
    }

    @Test
    public void testParseMathFunction(){
        message.setContent("b: command sqrt(5)");
        IntermediateCommand command = parser.parse(message);

        assertThat(command.getArguments()).hasSize(1);
        assertThat(command.getArguments().get(0)).isInstanceOf(ASTExpressionArgument.class);
        assertThat(command.getName()).isEqualTo("command");
        assertThat(command.getPrefix()).contains("b");
    
        Argument argument = command.getArguments().get(0);
        assertThat(argument.asNumber().map(BigDecimal::doubleValue)).contains(Math.sqrt(5));
    }

    @Test
    public void testParseFlags(){
        message.setContent("b: command -Flag Argument");
        IntermediateCommand command = parser.parse(message);

        assertThat(command.getFlags()).containsExactly("Flag");
        assertThat(command.getName()).isEqualTo("command");
        assertThat(command.getPrefix()).contains("b");
    }
}
