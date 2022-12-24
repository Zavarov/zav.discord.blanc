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
