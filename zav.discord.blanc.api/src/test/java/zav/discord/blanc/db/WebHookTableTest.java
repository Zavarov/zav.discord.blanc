package zav.discord.blanc.db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.databind.WebHookValueObject;

/**
 * Test case for the WebHook database.<br>
 * Verifies that entries are written and read correctly.
 */
public class WebHookTableTest extends AbstractTest {
  
  /**
   * Deserializes all Discord entities and initializes the WebHook database.
   *
   * @throws SQLException If a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    super.setUp();
  
    WebHookTable.create();
  }
  
  @Test
  public void testCreateOverExistingTable() throws SQLException {
    // Table has already been created in setUp()
    assertThat(WebHookTable.put(guild, channel, hook)).isEqualTo(1);
    assertThat(WebHookTable.contains(guild.getId(), channel.getId(), hook.getId())).isTrue();
    // Should not replace the existing DB
    WebHookTable.create();
    assertThat(WebHookTable.contains(guild.getId(), channel.getId(), hook.getId())).isTrue();
  }
  
  @Test
  public void testContains() throws SQLException {
    assertThat(WebHookTable.contains(guild.getId(), channel.getId(), hook.getId())).isFalse();
    assertThat(WebHookTable.put(guild, channel, hook)).isEqualTo(1);
    assertThat(WebHookTable.contains(guild.getId(), channel.getId(), hook.getId())).isTrue();
  }
  
  @Test
  public void testPut() throws SQLException {
    // One row has been modified
    assertThat(WebHookTable.put(guild, channel, hook)).isEqualTo(1);
  }
  
  @Test
  public void testPutAlreadyExistingHook() throws SQLException {
    WebHookTable.put(guild, channel, hook);
  
    WebHookValueObject response = WebHookTable.get(guild.getId(), channel.getId(), hook.getId());
    assertThat(hook.getName()).isEqualTo(response.getName());
  
    hook.setName("Updated");
    
    WebHookTable.put(guild, channel, hook);
    response = WebHookTable.get(guild.getId(), channel.getId(), hook.getId());
    // Old row has been updated
    assertThat(hook.getName()).isEqualTo(response.getName());
  }
  
  @Test
  public void testDelete() throws SQLException {
    assertThat(WebHookTable.contains(guild.getId(), channel.getId(), hook.getId())).isFalse();
    assertThat(WebHookTable.put(guild, channel, hook)).isEqualTo(1);
    assertThat(WebHookTable.contains(guild.getId(), channel.getId(), hook.getId())).isTrue();
    assertThat(WebHookTable.delete(guild.getId(), channel.getId(), hook.getId())).isEqualTo(1);
    assertThat(WebHookTable.contains(guild.getId(), channel.getId(), hook.getId())).isFalse();
  }
  
  @Test
  public void testDeleteAll() throws SQLException {
    assertThat(WebHookTable.contains(guild.getId(), channel.getId(), hook.getId())).isFalse();
    assertThat(WebHookTable.put(guild, channel, hook)).isEqualTo(1);
    assertThat(WebHookTable.contains(guild.getId(), channel.getId(), hook.getId())).isTrue();
    assertThat(WebHookTable.deleteAll(guild.getId())).isEqualTo(1);
    assertThat(WebHookTable.contains(guild.getId(), channel.getId(), hook.getId())).isFalse();
  }
  
  @Test
  public void testDeleteUnknownHook() throws SQLException {
    // hook doesn't exist => Nothing to remove
    assertThat(WebHookTable.delete(guild.getId(), channel.getId(), hook.getId())).isEqualTo(0);
  }
  
  @Test
  public void testGetHook() throws SQLException {
    WebHookTable.put(guild, channel, hook);
  
    WebHookValueObject response = WebHookTable.get(guild.getId(), channel.getId(), hook.getId());
    
    assertThat(response.getId()).isEqualTo(hook.getId());
    assertThat(response.getChannelId()).isEqualTo(hook.getChannelId());
    assertThat(response.getName()).isEqualTo(hook.getName());
    assertThat(response.getSubreddits()).isEqualTo(hook.getSubreddits());
  }
  
  @Test
  public void testGetAllHooks() throws SQLException {
    WebHookTable.put(guild, channel, hook);
  
    List<WebHookValueObject> responses = WebHookTable.getAll(guild.getId());
  
    assertThat(responses).hasSize(1);
  
    WebHookValueObject response = responses.get(0);
  
    assertThat(response.getId()).isEqualTo(hook.getId());
    assertThat(response.getChannelId()).isEqualTo(hook.getChannelId());
    assertThat(response.getName()).isEqualTo(hook.getName());
    assertThat(response.getSubreddits()).isEqualTo(hook.getSubreddits());
    assertThat(response.isOwner()).isEqualTo(hook.isOwner());
  }
  
  @Test
  public void testGetUnknownHook() {
    long guildId = guild.getId();
    long channelId = channel.getId();
    long hookId = hook.getId();
    assertThrows(NoSuchElementException.class, () -> WebHookTable.get(guildId, channelId, hookId));
  }
}
