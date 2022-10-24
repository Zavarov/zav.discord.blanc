package zav.discord.blanc.api.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
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
}
