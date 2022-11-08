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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.List;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.api.Site.Group;

/**
 * Test class for interactive messages.
 */
public class SiteTest {
  Site site1;
  Site site2;

  MessageEmbed content1;
  MessageEmbed content2;
  MessageEmbed content3;
  
  Site.Page page1;
  Site.Page page2;
  Site.Page page3;
  
  Site.Group group;
  
  User owner;
  
  /**
   * Creates a site with 2 pages. The first page contains 3 entries, the second page only 1 entry.
   */
  @BeforeEach
  public void setUp() {
    content1 = mock(MessageEmbed.class);
    content2 = mock(MessageEmbed.class);
    content3 = mock(MessageEmbed.class);
    
    page1 = Site.Page.create(content1);
    page2 = Site.Page.create(content2);
    page3 = Site.Page.create(content3);
  
    owner = mock(User.class);
    
    site1 = Site.create(List.of(page1, page2, page3), "site1");
    site2 = Site.create(List.of(page3, page2, page1), "site2");
    group = Group.create(List.of(site1, site2), owner);
  }
  
  @Test
  public void testMoveLeft() {
    // Roll over
    site1.moveLeft();
    assertEquals(site1.getCurrentPage(), content3);
    site1.moveLeft();
    assertEquals(site1.getCurrentPage(), content2);
    site1.moveLeft();
    assertEquals(site1.getCurrentPage(), content1);
  }
  
  @Test
  public void testMoveRight() {
    site1.moveRight();
    assertEquals(site1.getCurrentPage(), content2);
    site1.moveRight();
    assertEquals(site1.getCurrentPage(), content3);
    // Roll over
    site1.moveRight();
    assertEquals(site1.getCurrentPage(), content1);
  }
  
  @Test
  public void testChangeSelection() {
    group.changeSelection("site1");
    assertEquals(group.getCurrentSite(), site1);
    group.changeSelection("site2");
    assertEquals(group.getCurrentSite(), site2);
  }
  
  @Test
  public void testGetOwner() {
    assertEquals(group.getOwner(), owner);
  }
}
