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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import zav.discord.blanc.api.Parameter;
import zav.discord.blanc.command.IntermediateCommand;
import zav.discord.blanc.mc.callable._parser.CallableParser;
import zav.discord.blanc.mc.parameter._ast.ASTExpressionParameter;
import zav.discord.blanc.mc.parameter._ast.ASTRoleParameter;
import zav.discord.blanc.mc.parameter._ast.ASTStringParameter;
import zav.discord.blanc.mc.parameter._ast.ASTTextChannelParameter;
import zav.discord.blanc.mc.parameter._ast.ASTUserParameter;

/**
 * Test case for the command parser.<br>
 * It verifies whether a correct intermediate command is created from a raw string.
 */
public class MontiCoreCommandParserTest {
  MontiCoreCommandParser parser;
  Message message;

  @BeforeEach
  public void setUp() {
    parser = new MontiCoreCommandParser();
    message = mock(Message.class);
  }

  @Test
  public void testParseEmpty() {
    when(message.getContentRaw()).thenReturn(StringUtils.EMPTY);
    @Nullable IntermediateCommand command = parser.parse(message);
    
    assertThat(command).isNull();
  }

  @Test
  public void testParseName() {
    when(message.getContentRaw()).thenReturn("b: command Parameter");
    @Nullable IntermediateCommand command = parser.parse(message);

    assertThat(command).isNotNull();
    assertThat(command.getParameters()).hasSize(1);
    assertThat(command.getParameters().get(0)).isInstanceOf(ASTExpressionParameter.class);
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");

    Parameter parameter = command.getParameters().get(0);
    assertThat(parameter.asString()).contains("Parameter");
  }

  @Test
  public void testParseString() {
    when(message.getContentRaw()).thenReturn("b: command \"12345\"");
    @Nullable IntermediateCommand command = parser.parse(message);
  
    assertThat(command).isNotNull();
    assertThat(command.getParameters()).hasSize(1);
    assertThat(command.getParameters().get(0)).isInstanceOf(ASTStringParameter.class);
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");

    Parameter parameter = command.getParameters().get(0);
    assertThat(parameter.asString()).contains("12345");
  }
  
  @Test
  public void testParseInvalidRole() {
    when(message.getContentRaw()).thenReturn("b: command <@&>");
    @Nullable IntermediateCommand command = parser.parse(message);
    assertThat(command).isNull();
    
    when(message.getContentRaw()).thenReturn("b: command <@&1.2345>");
    command = parser.parse(message);
    assertThat(command).isNull();
  }

  @Test
  public void testParseRole() {
    when(message.getContentRaw()).thenReturn("b: command <@&12345>");
    @Nullable IntermediateCommand command = parser.parse(message);
  
    assertThat(command).isNotNull();
    assertThat(command.getParameters()).hasSize(1);
    assertThat(command.getParameters().get(0)).isInstanceOf(ASTRoleParameter.class);
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");

    Parameter parameter = command.getParameters().get(0);
    assertThat(parameter.asNumber().map(BigDecimal::longValue)).contains(12345L);
  }
  
  @Test
  public void testParseInvalidTextChannel() {
    when(message.getContentRaw()).thenReturn("b: command <#>");
    @Nullable IntermediateCommand command = parser.parse(message);
    assertThat(command).isNull();
    
    when(message.getContentRaw()).thenReturn("b: command <#1.2345>");
    command = parser.parse(message);
    assertThat(command).isNull();
  }

  @Test
  public void testParseTextChannel() {
    when(message.getContentRaw()).thenReturn("b: command <#12345>");
    @Nullable IntermediateCommand command = parser.parse(message);
  
    assertThat(command).isNotNull();
    assertThat(command.getParameters()).hasSize(1);
    assertThat(command.getParameters().get(0)).isInstanceOf(ASTTextChannelParameter.class);
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");

    Parameter parameter = command.getParameters().get(0);
    assertThat(parameter.asNumber().map(BigDecimal::longValue)).contains(12345L);
  }

  @Test
  public void testParseUser() {
    when(message.getContentRaw()).thenReturn("b: command <@12345>");
    @Nullable IntermediateCommand command = parser.parse(message);
  
    assertThat(command).isNotNull();
    assertThat(command.getParameters()).hasSize(1);
    assertThat(command.getParameters().get(0)).isInstanceOf(ASTUserParameter.class);
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");

    Parameter parameter = command.getParameters().get(0);
    assertThat(parameter.asNumber().map(BigDecimal::longValue)).contains(12345L);
  }
  
  @Test
  public void testParseInvalidUser() {
    when(message.getContentRaw()).thenReturn("b: command <@>");
    @Nullable IntermediateCommand command = parser.parse(message);
    assertThat(command).isNull();
    
    when(message.getContentRaw()).thenReturn("b: command <@1.2345>");
    command = parser.parse(message);
    assertThat(command).isNull();
  }

  @Test
  public void testParseExpression() {
    when(message.getContentRaw()).thenReturn("b: command 5+3");
    @Nullable IntermediateCommand command = parser.parse(message);
  
    assertThat(command).isNotNull();
    assertThat(command.getParameters()).hasSize(1);
    assertThat(command.getParameters().get(0)).isInstanceOf(ASTExpressionParameter.class);
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");

    Parameter parameter = command.getParameters().get(0);
    assertThat(parameter.asNumber().map(BigDecimal::intValue)).contains(8);
  }

  @Test
  public void testParseMathFunction() {
    when(message.getContentRaw()).thenReturn("b: command sqrt(5)");
    @Nullable IntermediateCommand command = parser.parse(message);
  
    assertThat(command).isNotNull();
    assertThat(command.getParameters()).hasSize(1);
    assertThat(command.getParameters().get(0)).isInstanceOf(ASTExpressionParameter.class);
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");

    Parameter parameter = command.getParameters().get(0);
    assertThat(parameter.asNumber().map(BigDecimal::doubleValue)).contains(Math.sqrt(5));
  }

  @Test
  public void testParseFlags() {
    when(message.getContentRaw()).thenReturn("b: command -Flag Parameter");
    @Nullable IntermediateCommand command = parser.parse(message);
  
    assertThat(command).isNotNull();
    assertThat(command.getFlags()).containsExactly("Flag");
    assertThat(command.getName()).isEqualTo("command");
    assertThat(command.getPrefix()).contains("b");
  }
  
  @Test
  public void testParseInvalidString() {
    try (MockedConstruction<CallableParser> ignored = mockConstruction(CallableParser.class,
          (c, i) -> when(c.parse_String(anyString())).thenThrow(new IOException()))) {
      
      when(message.getContentRaw()).thenReturn("%s%s");
      @Nullable IntermediateCommand command = new MontiCoreCommandParser().parse(message);
      assertThat(command).isNull();
    }
  }
}