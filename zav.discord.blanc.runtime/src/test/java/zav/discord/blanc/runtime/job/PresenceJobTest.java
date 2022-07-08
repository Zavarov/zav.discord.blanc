package zav.discord.blanc.runtime.job;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.managers.Presence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Client;

/**
 * This test case checks whether the status message for all shards are properly updated.
 */
@ExtendWith(MockitoExtension.class)
public class PresenceJobTest {
  @Mock JDA jda1;
  @Mock JDA jda2;
  @Mock Client client;
  @Mock Presence presence;
  PresenceJob job;
  
  /**
   * Initializes the presence job with a mocked Discord client over two shards.
   *
   * @throws IOException Should never be thrown.
   */
  @BeforeEach
  public void setUp() throws IOException {
    when(client.getShards()).thenReturn(List.of(jda1, jda2));
    when(jda1.getPresence()).thenReturn(presence);
    when(jda2.getPresence()).thenReturn(presence);
    job = new PresenceJob(client);
  }
  
  @Test
  public void testRun() { 
    job.run();
    
    verify(presence, times(2)).setActivity(any());
  }
}
