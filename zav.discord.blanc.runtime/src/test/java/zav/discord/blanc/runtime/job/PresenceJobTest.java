package zav.discord.blanc.runtime.job;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.runtime.command.AbstractTest;

/**
 * This test case checks whether the status message for all shards are properly updated.
 */
@ExtendWith(MockitoExtension.class)
public class PresenceJobTest extends AbstractTest {
  PresenceJob job;
  
  /**
   * Initializes the presence job with a mocked Discord client over two shards.
   *
   * @throws IOException Should never be thrown.
   */
  @BeforeEach
  public void setUp() throws IOException {
    job = new PresenceJob(client);
  }
  
  @Test
  public void testRun() { 
    job.run();
    
    verify(presence).setActivity(any());
  }
}
