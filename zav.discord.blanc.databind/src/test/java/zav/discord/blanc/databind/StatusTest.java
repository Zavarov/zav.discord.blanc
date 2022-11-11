package zav.discord.blanc.databind;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StatusTest {
  Status status;
  
  @BeforeEach
  public void setUp() throws IOException {
    status = Status.read(StatusTest.class.getClassLoader(), "Status.json");
  }
  
  @Test
  public void testGetMessages() {
    assertEquals(status.getMessages(), Arrays.asList("Foo", "Bar"));
  }
}
