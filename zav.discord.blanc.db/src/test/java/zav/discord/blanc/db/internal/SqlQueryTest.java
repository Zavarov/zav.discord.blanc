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

package zav.discord.blanc.db.internal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static zav.discord.blanc.db.internal.SqlQuery.GUILD_DB;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Checks whether invalid queries throw the correct exceptions.
 */
public class SqlQueryTest {
  
  SqlQuery query;
  
  @BeforeEach
  public void setUp() {
    // Any database is fine, we just need something to specify the correct driver
    query = new SqlQuery(GUILD_DB);
  }
  
  @Test
  public void testQueryWithUnknownStatement() {
    assertThatThrownBy(() -> query.query("unknown"))
          .isInstanceOf(IllegalArgumentException.class);
  }
  
  @Test
  public void testDeserializeInvalidObject() {
    assertThatThrownBy(() -> SqlQuery.deserialize(new Object()))
          .isInstanceOf(IllegalArgumentException.class);
  }
  
  @Test
  public void testMarshallInvalidObject() {
    assertThatThrownBy(() -> SqlQuery.marshal(new Object()))
          .isInstanceOf(IllegalArgumentException.class);
  }
}
