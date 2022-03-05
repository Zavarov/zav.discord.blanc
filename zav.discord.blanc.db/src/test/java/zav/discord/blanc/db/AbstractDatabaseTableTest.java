/*
 * Copyright (c) 2021 Zavarov.
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static zav.discord.blanc.db.sql.SqlQuery.ENTITY_DB_PATH;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Base class for all test suites.<br>
 * Initializes all databases.
 */
@ExtendWith(MockitoExtension.class)
public abstract class AbstractDatabaseTableTest {
  protected Injector guice;
  protected @Mock AbstractDatabaseTable<Object> db;
  
  /**
   * Deserializes Discord instances.
   *
   * @throws SQLException If a database error occurred.
   */
  @BeforeEach
  public void setUp() throws SQLException {
    guice = Guice.createInjector();
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
  
  protected <T> T get(DatabaseTable<T> db, Object... keys) throws SQLException {
    List<T> response = db.get(keys);
    assertThat(response).hasSize(1);
    return response.get(0);
  }
  
  @Test
  public void testPostConstruct() throws Exception {
    doThrow(new SQLException()).when(db).create();
    doCallRealMethod().when(db).postConstruct();
    
    assertThatThrownBy(() -> db.postConstruct()).isInstanceOf(SQLException.class);
  }
}
