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

package zav.discord.blanc.api.internal;


import static zav.discord.blanc.api.Constants.SITE;

import com.google.common.cache.Cache;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Site;

/**
 * The listener for notifying the message components of a command whenever the author interacts with
 * it.
 */
@NonNullByDefault
public class SiteComponentListener extends ListenerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(SiteComponentListener.class);
  
  @Inject
  @Named(SITE)
  private Cache<Message, Site> cache;
  
  /*package*/ SiteComponentListener() {
    // Create instance with Guice
  }
  
  @Override
  public void onButtonClick(@NonNull ButtonClickEvent event) {
    @Nullable Site site = cache.getIfPresent(event.getMessage());
    
    // Unknown message -> ignore
    if (site == null) {
      return;
    }
    
    // Invalid user -> reject
    if (!site.getOwner().equals(event.getUser())) {
      event.getInteraction().deferReply().complete();
      return;
    }
    
    @Nullable Button button = event.getButton();
    
    // Null for ephemeral messages
    if (button == null) {
      return;
    }
    
    @Nullable String id = button.getId();
    
    // Null if absent
    if (id == null) {
      return;
    }
    
    switch (id) {
      case "left":
        site.moveLeft();
        event.getInteraction()
              .deferEdit()
              .setEmbeds(site.getCurrentPage())
              .complete();
        break;
      case "right":
        site.moveRight();
        event.getInteraction()
              .deferEdit()
              .setEmbeds(site.getCurrentPage())
              .complete();
        break;
      default:
        LOGGER.error("unknown id '{}'.", id);
    }
  }
  
  @Override
  public void onSelectionMenu(@NonNull SelectionMenuEvent event) {
    @Nullable Site site = cache.getIfPresent(event.getMessage());
  
    // Unknown message -> ignore
    if (site == null) {
      return;
    }
  
    // Invalid user -> reject
    if (!site.getOwner().equals(event.getUser())) {
      event.getInteraction().deferReply().complete();
      return;
    }
    
    List<String> values = event.getValues();
    
    // Only a single entry can be selected.
    if (values.size() != 1) {
      return;
    }
    
    site.changeSelection(values.get(0));
    event.getInteraction()
          .deferEdit()
          .setEmbeds(site.getCurrentPage())
          .complete();
  }
}
