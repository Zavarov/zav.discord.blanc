/*
 * Copyright (c) 2022 Zavarov.
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

package zav.discord.blanc.runtime.command.core;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.runtime.command.AbstractCommandTest;

/**
 * Checks whether the correct answer is returned for a given arithmetic expression.
 */
@ExtendWith(MockitoExtension.class)
public class MathCommandTest extends AbstractCommandTest {
  private static final BigDecimal expected = BigDecimal.valueOf(Math.sin(Math.PI));
  private static final Percentage offset = Percentage.withPercentage(1e-15);
  
  private @Mock MessageAction action;

  @Test
  public void testCommandIsOfCorrectType() {
    check("b:math sin(pi)", MathCommand.class);
  }
  
  @Test
  public void testSendSolution() throws Exception {
    when(textChannel.sendMessage(any(CharSequence.class))).thenReturn(action);
    
    run("b:math sin(pi)");
    
    ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
    verify(textChannel, times(1)).sendMessage(stringCaptor.capture());
    assertThat(new BigDecimal(stringCaptor.getValue())).isCloseTo(expected, offset);
  }
}
