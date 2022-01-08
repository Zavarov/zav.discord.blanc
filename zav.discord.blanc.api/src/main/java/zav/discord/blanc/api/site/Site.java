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

package zav.discord.blanc.api.site;

import java.util.List;
import java.util.function.Consumer;
import org.apache.commons.lang3.Validate;
import zav.discord.blanc.databind.message.PageDto;
import zav.discord.blanc.databind.message.SiteDto;

/**
 * Implementation of site.
 */
public class Site implements SiteListener {
  private final List<SiteDto> sites;
  private int index;
  private SiteDto currentSite;
  private PageDto currentPage;
  
  private Site(List<SiteDto> sites) {
    this.sites = sites;
    this.index = 0;
    this.currentSite = sites.get(0);
    this.currentPage = this.currentSite.getPages().get(0);
  }
  
  /**
   * Creates a new instance of a site over the provided arguments.<br>
   * The argument has to contain at least one element. Furthermore, all sites need to contain at
   * least one page.
   *
   * @param sites All sites of this object.
   * @return A new site instance over the argument.
   */
  public static Site of(List<SiteDto> sites) {
    Validate.validIndex(sites, 0);
    for (SiteDto site : sites) {
      Validate.validIndex(site.getPages(), 0);
    }
    return new Site(sites);
  }
  
  @Override
  public boolean canMoveLeft() {
    return index > 0;
  }
  
  @Override
  public void moveLeft(Consumer<PageDto> consumer) {
    currentSite = sites.get(--index);
    currentPage = currentSite.getPages().get(0);
    
    consumer.accept(currentPage);
  }
  
  @Override
  public boolean canMoveRight() {
    return index < currentSite.getPages().size() - 1;
  }
  
  @Override
  public void moveRight(Consumer<PageDto> consumer) {
    currentSite = sites.get(++index);
    currentPage = currentSite.getPages().get(0);
    
    consumer.accept(currentPage);
  }
  
  @Override
  public void changeSelection(String label, Consumer<PageDto> consumer) {
    index = 0;
    
    currentSite = sites.stream()
          .filter(site -> site.getLabel().equals(label))
          .findFirst()
          .orElseThrow();
    currentPage = currentSite.getPages().get(0);
    
    consumer.accept(currentPage);
  }
}
