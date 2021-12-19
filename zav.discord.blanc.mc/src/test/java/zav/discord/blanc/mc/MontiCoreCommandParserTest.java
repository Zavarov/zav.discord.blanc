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

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.command.parser.IntermediateCommand;
import zav.discord.blanc.command.parser.Parser;
import zav.discord.blanc.databind.MessageValueObject;
import zav.discord.blanc.mc.argument._ast.ASTExpressionArgument;
import zav.discord.blanc.mc.argument._ast.ASTRoleArgument;
import zav.discord.blanc.mc.argument._ast.ASTStringArgument;
import zav.discord.blanc.mc.argument._ast.ASTTextChannelArgument;
import zav.discord.blanc.mc.argument._ast.ASTUserArgument;

/**
 * Test case for the command parser.<br>
 * It verifies whether a correct intermediate command is created from a raw string.
 */
public class MontiCoreCommandParserTest {
  Parser parser;
  MessageValueObject message;

  @BeforeEach
  public void setUp() {
    parser = new MontiCoreCommandParser();
    message = new MessageValueObject();
  }

  @Test
  public void testParseEmpty() {
    @Nullable IntermediateCommand command = parser.parse(message);
    
    assertThat(command).isNull();
  }

  @Test
  public void testParseName() {
    message.setContent("b: command Argument");
    @Nullable IntermediateCommand command = parser.parse(message);

    assertThat(command).isNotNull();
    assertThat(command.getArguments()).hasSize(1);
    assertThat(command.getArguments().get(0)).isInstanceOf(ASTExpressionArgument.class);
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");

    Argument argument = command.getArguments().get(0);
    assertThat(argument.asString()).contains("Argument");
  }

  @Test
  public void testParseString() {
    message.setContent("b: command \"12345\"");
    @Nullable IntermediateCommand command = parser.parse(message);
  
    assertThat(command).isNotNull();
    assertThat(command.getArguments()).hasSize(1);
    assertThat(command.getArguments().get(0)).isInstanceOf(ASTStringArgument.class);
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");

    Argument argument = command.getArguments().get(0);
    assertThat(argument.asString()).contains("12345");
  }

  @Test
  public void testParseRole() {
    message.setContent("b: command <@&12345>");
    @Nullable IntermediateCommand command = parser.parse(message);
  
    assertThat(command).isNotNull();
    assertThat(command.getArguments()).hasSize(1);
    assertThat(command.getArguments().get(0)).isInstanceOf(ASTRoleArgument.class);
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");

    Argument argument = command.getArguments().get(0);
    assertThat(argument.asNumber().map(BigDecimal::longValue)).contains(12345L);
  }

  @Test
  public void testParseTextChannel() {
    message.setContent("b: command <#12345>");
    @Nullable IntermediateCommand command = parser.parse(message);
  
    assertThat(command).isNotNull();
    assertThat(command.getArguments()).hasSize(1);
    assertThat(command.getArguments().get(0)).isInstanceOf(ASTTextChannelArgument.class);
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");

    Argument argument = command.getArguments().get(0);
    assertThat(argument.asNumber().map(BigDecimal::longValue)).contains(12345L);
  }

  @Test
  public void testParseUser() {
    message.setContent("b: command <@12345>");
    @Nullable IntermediateCommand command = parser.parse(message);
  
    assertThat(command).isNotNull();
    assertThat(command.getArguments()).hasSize(1);
    assertThat(command.getArguments().get(0)).isInstanceOf(ASTUserArgument.class);
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");

    Argument argument = command.getArguments().get(0);
    assertThat(argument.asNumber().map(BigDecimal::longValue)).contains(12345L);
  }

  @Test
  public void testParseExpression() {
    message.setContent("b: command 5+3");
    @Nullable IntermediateCommand command = parser.parse(message);
  
    assertThat(command).isNotNull();
    assertThat(command.getArguments()).hasSize(1);
    assertThat(command.getArguments().get(0)).isInstanceOf(ASTExpressionArgument.class);
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");

    Argument argument = command.getArguments().get(0);
    assertThat(argument.asNumber().map(BigDecimal::intValue)).contains(8);
  }

  @Test
  public void testParseMathFunction() {
    message.setContent("b: command sqrt(5)");
    @Nullable IntermediateCommand command = parser.parse(message);
  
    assertThat(command).isNotNull();
    assertThat(command.getArguments()).hasSize(1);
    assertThat(command.getArguments().get(0)).isInstanceOf(ASTExpressionArgument.class);
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");

    Argument argument = command.getArguments().get(0);
    assertThat(argument.asNumber().map(BigDecimal::doubleValue)).contains(Math.sqrt(5));
  }

  @Test
  public void testParseFlags() {
    message.setContent("b: command -Flag Argument");
    @Nullable IntermediateCommand command = parser.parse(message);
  
    assertThat(command).isNotNull();
    assertThat(command.getFlags()).containsExactly("Flag");
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");
  }
}