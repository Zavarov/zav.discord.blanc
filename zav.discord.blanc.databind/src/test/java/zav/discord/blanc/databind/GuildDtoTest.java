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

package zav.discord.blanc.databind;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Checks whether the correct pattern is derived from a list of banned expressions.
 */
public class GuildDtoTest {
  GuildDto dto;
  
  @BeforeEach
  public void setUp() {
    dto = new GuildDto();
  }
  
  @Test
  public void testGetPattern() {
    assertThat(dto.getPattern()).isNull();
    dto.setBlacklist(List.of("foo"));
    assertThat(dto.getPattern()).asString().isEqualTo("foo");
    dto.setBlacklist(List.of("foo", "bar"));
    assertThat(dto.getPattern()).asString().isEqualTo("foo|bar");
  }
}
