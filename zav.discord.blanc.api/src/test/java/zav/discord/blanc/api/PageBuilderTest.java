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
