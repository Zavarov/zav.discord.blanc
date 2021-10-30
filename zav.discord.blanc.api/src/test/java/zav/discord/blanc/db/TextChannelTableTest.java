package zav.discord.blanc.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.databind.TextChannel;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TextChannelTableTest extends AbstractTest {
  @BeforeEach
  public void setUp() throws SQLException {
    super.setUp();
  
    TextChannelTable.create();
  }
  
  @Test
  public void testCreateOverExistingTable() throws SQLException {
    // Table has already been created in setUp()
    assertThat(TextChannelTable.put(guild, channel)).isEqualTo(1);
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isTrue();
    // Should not replace the existing DB
    TextChannelTable.create();
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isTrue();
  }
  
  @Test
  public void testContains() throws SQLException {
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isFalse();
    assertThat(TextChannelTable.put(guild, channel)).isEqualTo(1);
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isTrue();
  }
  
  @Test
  public void testPut() throws SQLException {
    // One row has been modified
    assertThat(TextChannelTable.put(guild, channel)).isEqualTo(1);
  }
  
  @Test
  public void testPutAlreadyExistingChannel() throws SQLException {
    TextChannelTable.put(guild, channel);
  
    TextChannel response = TextChannelTable.get(guild.getId(), channel.getId());
    assertThat(channel.getName()).isEqualTo(response.getName());
  
    channel.setName("Updated");
    
    TextChannelTable.put(guild, channel);
    response = TextChannelTable.get(guild.getId(), channel.getId());
    // Old row has been updated
    assertThat(channel.getName()).isEqualTo(response.getName());
  }
  
  @Test
  public void testDelete() throws SQLException {
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isFalse();
    assertThat(TextChannelTable.put(guild, channel)).isEqualTo(1);
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isTrue();
    assertThat(TextChannelTable.delete(guild.getId(), channel.getId())).isEqualTo(1);
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isFalse();
  }
  
  @Test
  public void testDeleteAll() throws SQLException {
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isFalse();
    assertThat(TextChannelTable.put(guild, channel)).isEqualTo(1);
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isTrue();
    assertThat(TextChannelTable.deleteAll(guild.getId())).isEqualTo(1);
    assertThat(TextChannelTable.contains(guild.getId(), channel.getId())).isFalse();
  }
  
  @Test
  public void testDeleteUnknownChannel() throws SQLException {
    // channel doesn't exist => Nothing to remove
    assertThat(TextChannelTable.delete(guild.getId(), channel.getId())).isEqualTo(0);
  }
  
  @Test
  public void testGetChannel() throws SQLException {
    TextChannelTable.put(guild, channel);
  
    TextChannel response = TextChannelTable.get(guild.getId(), channel.getId());
    
    assertThat(response.getId()).isEqualTo(channel.getId());
    assertThat(response.getName()).isEqualTo(channel.getName());
    assertThat(response.getSubreddits()).isEqualTo(channel.getSubreddits());
  }
  
  @Test
  public void testGetUnknownChannel() {
    assertThrows(NoSuchElementException.class, () -> TextChannelTable.get(guild.getId(), channel.getId()));
  }
}
