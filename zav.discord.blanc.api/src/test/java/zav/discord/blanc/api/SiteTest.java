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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.MessageEmbed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for interactive messages.
 */
public class SiteTest {
  Site site;
  
  List<MessageEmbed> entries1;
  Site.Page page1;
  
  List<MessageEmbed> entries2;
  Site.Page page2;
  
  /**
   * Creates a site with 2 pages. The first page contains 3 entries, the second page only 1 entry.
   */
  @BeforeEach
  public void setUp() {
    entries1 = new ArrayList<>();
    entries1.add(mock(MessageEmbed.class));
    entries1.add(mock(MessageEmbed.class));
    entries1.add(mock(MessageEmbed.class));
    page1 = Site.Page.create("page1", entries1);

    entries2 = new ArrayList<>();
    entries2.add(mock(MessageEmbed.class));
    page2 = Site.Page.create("page2", entries2);
    
    site = Site.create(List.of(page1, page2));
  }
  
  @Test
  public void testMoveLeft() {
    // Roll over
    site.moveLeft(entry -> assertThat(entry).isEqualTo(entries1.get(2)));
    site.moveLeft(entry -> assertThat(entry).isEqualTo(entries1.get(1)));
    site.moveLeft(entry -> assertThat(entry).isEqualTo(entries1.get(0)));
  }
  
  @Test
  public void testMoveRight() {
    site.moveRight(entry -> assertThat(entry).isEqualTo(entries1.get(1)));
    site.moveRight(entry -> assertThat(entry).isEqualTo(entries1.get(2)));
    // Roll over
    site.moveRight(entry -> assertThat(entry).isEqualTo(entries1.get(0)));
  }
  
  @Test
  public void testChangeSelection() {
    site.changeSelection("page1", entry -> assertThat(entry).isEqualTo(entries1.get(0)));
    site.changeSelection("page2", entry -> assertThat(entry).isEqualTo(entries2.get(0)));
  }
}
