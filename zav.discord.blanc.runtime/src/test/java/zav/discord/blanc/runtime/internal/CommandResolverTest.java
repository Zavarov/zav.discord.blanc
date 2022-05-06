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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.junit.jupiter.api.Test;

/**
 * Checks whether the commands are correctly deserialized.
 */
public class CommandResolverTest {
  @Test
  public void testGetCommands() {
    List<CommandData> commands = CommandResolver.getCommands();
    
    // Should match the content of the JSON files
    assertThat(commands).hasSize(4);
    assertThat(commands.get(0).getName()).isEqualTo("math");
    assertThat(commands.get(1).getName()).isEqualTo("support");
    assertThat(commands.get(2).getName()).isEqualTo("dev");
    assertThat(commands.get(2).getSubcommands()).hasSize(5);
    assertThat(commands.get(3).getName()).isEqualTo("mod");
    assertThat(commands.get(3).getSubcommands()).hasSize(1);
    assertThat(commands.get(3).getSubcommandGroups()).hasSize(2);
  }
}
