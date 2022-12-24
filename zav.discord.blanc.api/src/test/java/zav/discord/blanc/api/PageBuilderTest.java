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

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import zav.discord.blanc.api.Site.Page;

/**
 * This test case verifies that the correct amount of pages are generated for a given set of items.
 */
public class PageBuilderTest {
  Site.Page.Builder builder;
  
  @BeforeEach
  public void setUp() {
    builder = new Site.Page.Builder("Test");
  }
  
  @Test
  public void testBuildFormatPage() {
    builder.add("Page {0}", 1);
    builder.add("Page {0}", 2);
    builder.add("Page {0}", 3);
    builder.setItemsPerPage(2);
    
    List<Page> pages = builder.build();
    assertEquals(pages.size(), 2);
  }
  
  @Test
  public void testBuildPage() {
    builder.add("Page 1");
    builder.add("Page 2");
    builder.add("Page 3");
    builder.setItemsPerPage(1);
    
    List<Page> pages = builder.build();
    assertEquals(pages.size(), 3);
  }
  
  @Test
  public void testBuildEmptyPage() {
    List<Page> pages = builder.build();
    assertEquals(pages.size(), 0);
  }
}
