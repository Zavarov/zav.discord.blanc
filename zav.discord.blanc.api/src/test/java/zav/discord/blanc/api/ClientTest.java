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

package zav.discord.blanc.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.Iterator;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.api.internal.JdaShardSupplier;

/**
 * Test class for the shards over which the client is split.
 */
public class ClientTest {
  List<JDA> shards;
  Client client;
  
  /**
   * Initialize a new client instance over 2 shards.
   */
  @BeforeEach
  public void setUp() {
    shards = List.of(mock(JDA.class), mock(JDA.class));
    
    Iterator<JDA> it = shards.iterator();
    
    JdaShardSupplier supplier = mock(JdaShardSupplier.class);
    doAnswer(invocation -> {
      it.forEachRemaining(invocation.getArgument(0));
      return null;
    }).when(supplier).forEachRemaining(any());
    
    client = new Client();
    client.postConstruct(supplier);
  }
  
  @Test
  public void testGetShards() {
    assertThat(client.getShards()).isEqualTo(shards);
  }
  
  @Test
  public void testGetShard() {
    assertThat(client.getShard(1 << 20)).isEqualTo(shards.get(0));
    assertThat(client.getShard(1 << 21)).isEqualTo(shards.get(0));
    assertThat(client.getShard(1 << 22)).isEqualTo(shards.get(1));
    assertThat(client.getShard(1 << 23)).isEqualTo(shards.get(0));
    assertThat(client.getShard(1 << 24)).isEqualTo(shards.get(0));
    assertThat(client.getShard(1 << 24 | 1 << 22)).isEqualTo(shards.get(1));
  }
}
