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

package zav.discord.blanc.runtime.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

/**
 * This test case checks whether all entities can be properly deserialized.
 */
public class JsonUtilsTest {
  
  @Test
  public void testGetCommands() {
    List<CommandData> commands = JsonUtils.getCommands();
    
    // Should match the content of the JSON files
    assertEquals(commands.size(), 5);
    assertEquals(commands.get(0).getName(), "math");
    assertEquals(commands.get(1).getName(), "support");
    assertEquals(commands.get(2).getName(), "invite");
    assertEquals(commands.get(3).getName(), "dev");
    assertEquals(commands.get(3).getSubcommands().size(), 4);
    assertEquals(commands.get(4).getName(), "mod");
    assertEquals(commands.get(4).getSubcommands().size(), 0);
    assertEquals(commands.get(4).getSubcommandGroups().size(), 3);
  }
}
