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

import java.nio.file.Files;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.UserTable;
import zav.discord.blanc.db.sql.SqlQuery;

public abstract class AbstractDevCommandTest extends AbstractCommandTest {
  
  protected UserTable userTable;
  protected UserEntity userEntity;
  
  @Override
  @BeforeEach
  public void setUp() throws Exception {
    super.setUp();
    userTable = injector.getInstance(UserTable.class);
    userEntity = new UserEntity()
          .withDiscriminator(StringUtils.EMPTY)
          .withId(OWNER_ID)
          .withName(StringUtils.EMPTY)
          .withRanks(List.of(Rank.ROOT.name()));
  }
}
