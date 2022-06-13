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

package zav.discord.blanc.db;

import static zav.discord.blanc.db.sql.SqlQuery.ENTITY_DB_PATH;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.db.sql.SqlQuery;

/**
 * Base class for all test suites.<br>
 * Initializes all databases.
 */
@ExtendWith(MockitoExtension.class)
public abstract class AbstractTableTest {
  protected SqlQuery query;
  
  /**
   * Deserializes Discord instances.
   *
   * @throws Exception If the database couldn't be initialized.
   */
  @BeforeEach
  public void setUp() throws Exception {
    Files.deleteIfExists(ENTITY_DB_PATH);
    Files.deleteIfExists(ENTITY_DB_PATH.getParent());
    
    query = new SqlQuery();
  }
  
  /**
   * Delete all database files.
   *
   * @throws IOException If one of the databases couldn't be deleted.
   */
  @AfterEach
  public void cleanUp() throws IOException {
    Files.deleteIfExists(ENTITY_DB_PATH);
    Files.deleteIfExists(ENTITY_DB_PATH.getParent());
  }
}
