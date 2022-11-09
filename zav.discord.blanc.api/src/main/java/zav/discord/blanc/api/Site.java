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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.Validate;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.jetbrains.annotations.Contract;

/**
 * A site is a collection of pages. The user can flip between the pages using the left and right
 * arrows. If the user clicks left on the first page, it jumps to the last page and vice versa.
 */
@NonNullByDefault
public class Site {
  private final List<Page> pages;
  private final String label;
  private int index;
  
  private Site(List<Page> pages, String label) {
    this.pages = pages;
    this.label = label;
  }
  
  /**
   * Creates a new instance of a site over the provided arguments.<br>
   * The site needs to contain at least one page.
   * Only the owner should be allowed to interact with this site.
   *
   * @param pages All sites of this object.
   * @param label The unique name of this site.
   * @return A new site instance over the argument.
   */
  @Contract(pure = true)
  public static Site create(List<Page> pages, String label) {
    Validate.validIndex(pages, 0);
    return new Site(List.copyOf(pages), label);
  }
  
  /**
   * Moves one page to the left. If this is the first page, jumps to the last page.
   */
  @Contract(mutates = "this")
  public void moveLeft() {
    index = Math.floorMod(index - 1, pages.size());
  }
  
  /**
   * Moves one page to the right. If this is the last page, jumps to the first page.
   */
  @Contract(mutates = "this")
  public void moveRight() {
    index = Math.floorMod(index + 1, pages.size());
  }
  
  /**
   * Returns the currently selected page.
   *
   * @return As described.
   */
  @Contract(pure = true)
  public MessageEmbed getCurrentPage() {
    return pages.get(index).content;
  }
  
  /**
   * Returns the total number of pages in this site.
   *
   * @return As described.
   */
  public int getSize() {
    return pages.size();
  }
  
  /**
   * Returns the unique name of this site.
   *
   * @return As described.
   */
  public String getLabel() {
    return label;
  }
  
  /**
   * A group is a collection of sites. Each site 
   */
  public static class Group {
    private final Map<String, Site> sites;
    private final User owner;
    private Site currentSite;
    
    private Group(List<Site> sites, User owner) {
      this.sites = sites.stream().collect(Collectors.toMap(Site::getLabel, site -> site));
      this.currentSite = sites.get(0);
      this.owner = owner;
    }
    
    /**
     * Creates a group of sites. The owner of the group can flip between those sites via their
     * unique name. A group needs to contain at least one site.
     *
     * @param sites All sites contained by this group.
     * @param owner The user for which this site was created.
     * @return A group over all sites.
     */
    public static Group create(List<Site> sites, User owner) {
      Validate.validIndex(sites, 0);
      return new Group(sites, owner);
    }
    
    /**
     * Changes the selection to the site with the given label.
     *
     * @param label The unique name of the site.
     */
    public void changeSelection(String label) {
      currentSite = Objects.requireNonNull(sites.get(label));
    }
    
    /**
     * Returns the currently selected site.
     *
     * @return As described.
     */
    public Site getCurrentSite() {
      return currentSite;
    }
    
    /**
     * Returns the user for which this site was created.
     *
     * @return As described.
     */
    @Contract(pure = true)
    public User getOwner() {
      return owner;
    }
  }
  
  /**
   * A page within a given site. Each page contains of multiple entries through which the user
   * can flip.
   */
  public static class Page {
    private final MessageEmbed content;
    
    private Page(MessageEmbed content) {
      this.content = content;
    }
    
    /**
     * Creates a new page with the given content..
     *
     * @param content The content displayed by the page.
     * @return As described.
     */
    @Contract(pure = true)
    public static Page create(MessageEmbed content) {
      return new Page(content);
    }
    
    /**
     * Implements the builder pattern for pages.
     */
    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public static class Builder {
      private final String title;
      private int itemsPerPage;
      private List<String> items = new ArrayList<>();
      
      public Builder(String title) {
        this.title = title;
      }
      
      /**
       * Adds a new item to the page.
       *
       * @param item A human-readable string.
       */
      public void add(String item) {
        items.add(item);
      }
      
      /**
       * Adds a new item to the page.
       * 
       * @param pattern the pattern string
       * @param args object(s) to format
       */
      public void add(String pattern, Object... args) {
        items.add(MessageFormat.format(pattern, args));
      }
      
      /**
       * Sets the items which are displayed per page. If this number is smaller than the number of
       * added items, new pages are created as necessary.
       *
       * @param itemsPerPage The number of items per page.
       */
      public void setItemsPerPage(int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
      }

      /**
       * Creates all pages required to represent the given items.
       *
       * @return A list of pages.
       */
      public List<Site.Page> build() {
        if (items.isEmpty()) {
          return Collections.emptyList();
        }
        
        List<Site.Page> result = new ArrayList<>();
        
        for (List<String> chunk : ListUtils.partition(items, itemsPerPage)) {
          EmbedBuilder builder = new EmbedBuilder();
          
          StringBuilder description = new StringBuilder();
          
          for (String item : chunk) {
            description.append(item);
          }
          
          builder.setDescription(description.toString());
          builder.setTitle(title);
          
          result.add(Site.Page.create(builder.build()));
          
        }
        
        return Collections.unmodifiableList(result);
      }
    }
  }
}
