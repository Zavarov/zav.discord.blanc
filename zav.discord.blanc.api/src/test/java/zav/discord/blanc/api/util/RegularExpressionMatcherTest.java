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

package zav.discord.blanc.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.regex.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import zav.discord.blanc.databind.AutoResponseEntity;

/**
 * Test case for checking whether the correct response is returned for a given pattern.
 */
public class RegularExpressionMatcherTest {
  RegularExpressionMatcher matcher;
  AutoResponseEntity e1;
  AutoResponseEntity e2;
  AutoResponseEntity e3;
  AutoResponseEntity e4;
  AutoResponseEntity e5;
  
  /**
   * Initializes the matcher with five automatic responses.
   */
  @BeforeEach
  public void setUp() {
    e1 = new AutoResponseEntity();
    e1.setPattern("\\bcheat(s)?\\b");
    e1.setAnswer("#FAQ");
    
    e2 = new AutoResponseEntity();
    e2.setPattern("(ping)");
    e2.setAnswer("pong");
    
    e3 = new AutoResponseEntity();
    e3.setPattern("(?:mii~)");
    e3.setAnswer("nipah");
    
    e4 = new AutoResponseEntity();
    e4.setPattern("Hello There!");
    e4.setAnswer("General Kenobi!");
    
    e5 = new AutoResponseEntity();
    e5.setPattern("n(o)+");
    e5.setAnswer("yes");
    matcher = new RegularExpressionMatcher(List.of(e1, e2, e3, e4, e5));
  }
  
  @ParameterizedTest
  @CsvSource({
    "cheat,#FAQ",
    "before cheat after,#FAQ",
    "before cheat,#FAQ",
    "cheat after,#FAQ",
    "cheats after,#FAQ",
    "cHeAtS after,#FAQ",
    "ping,pong",
    "xpingx,pong",
    "pingping,pong",
    "ping ping,pong",
    "mii~,nipah",
    "Hello There!,General Kenobi!",
    "no,yes",
    "Nooooooo, yes"
  })
  public void testMatch(String source, String target) {
    assertEquals(matcher.match(source).orElse(null), target);
  }
  
  @Test
  public void testIgnoreUnrelatedString() {
    assertNull(matcher.match("xxx").orElse(null));
  }
  
  @Test
  public void testFindFirst() {
    Matcher mock = mock(Matcher.class);
    
    when(mock.group(anyString())).thenReturn(null);
    assertNull(matcher.findFirst(mock));
    
    when(mock.group(anyString())).thenReturn("cheats");
    assertEquals(matcher.findFirst(mock), "#FAQ");
  }
}
