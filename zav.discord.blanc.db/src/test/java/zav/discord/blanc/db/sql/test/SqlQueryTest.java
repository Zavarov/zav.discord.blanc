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

package zav.discord.blanc.db.sql.test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static zav.discord.blanc.db.sql.SqlQuery.ENTITY_DB_PATH;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Checks whether invalid queries throw the correct exceptions.
 */
public class SqlQueryTest {
  
  SqlQuery query;
  
  /**
   * Initializes an SQL query over an empty database.
   *
   * @throws IOException If the database couldn't be created.
   */
  @BeforeEach
  public void setUp() throws IOException {
    Files.deleteIfExists(ENTITY_DB_PATH);
    Files.deleteIfExists(ENTITY_DB_PATH.getParent());
    
    query = new SqlQuery();
  
    Files.createDirectories(ENTITY_DB_PATH.getParent());
    Files.createFile(ENTITY_DB_PATH);
  }
  
  @AfterEach
  public void tearDown() throws IOException {
    Files.deleteIfExists(ENTITY_DB_PATH);
    Files.deleteIfExists(ENTITY_DB_PATH.getParent());
  }
  
  @Test
  public void testUnknownStatement() {
    assertThatThrownBy(() -> query.query("unknown.sql"))
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
