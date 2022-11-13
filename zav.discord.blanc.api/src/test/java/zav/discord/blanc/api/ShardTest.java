package zav.discord.blanc.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import net.dv8tion.jda.api.JDA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Test case over an individual shard.
 */
@ExtendWith(MockitoExtension.class)
public class ShardTest {
  @Mock Client client;
  @Mock JDA jda;
  Shard shard;
  
  @BeforeEach
  public void setUp() {
    shard = new Shard(client, jda);
  }
  
  @Test
  public void testGetClient() {
    assertEquals(shard.getClient(), client);
  }
  
  @Test
  public void testGetJda() {
    assertEquals(shard.getJda(), jda);
  }
}
