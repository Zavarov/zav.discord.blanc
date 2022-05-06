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

package zav.discord.blanc.runtime.command;

import static zav.test.io.JsonUtils.read;

import java.sql.SQLException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.UserTable;

/**
 * Abstract base class for all developer commands.
 */
public abstract class AbstractDevCommandTest extends AbstractCommandTest {
  protected UserTable userTable;
  protected UserEntity userEntity;
  
  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    userEntity = read("User.json", UserEntity.class);
    userTable = injector.getInstance(UserTable.class);
    userTable.put(userEntity);
  }
}
