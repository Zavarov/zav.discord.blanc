package zav.discord.blanc.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.databind.WebHook;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WebHookTableTest extends AbstractTest {
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
  
    WebHook response = WebHookTable.get(guild.getId(), channel.getId(), hook.getId());
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
  
    WebHook response = WebHookTable.get(guild.getId(), channel.getId(), hook.getId());
    
    assertThat(response.getId()).isEqualTo(hook.getId());
    assertThat(response.getChannelId()).isEqualTo(hook.getChannelId());
    assertThat(response.getName()).isEqualTo(hook.getName());
    assertThat(response.getSubreddits()).isEqualTo(hook.getSubreddits());
  }
  
  @Test
  public void testGetUnknownHook() {
    assertThrows(NoSuchElementException.class, () -> WebHookTable.get(guild.getId(), channel.getId(), hook.getId()));
  }
}
