/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.api.internal.listener;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.Site;

/**
 * The listener for notifying the message components of a command whenever the author interacts with
 * it.
 */
public class SiteComponentListener extends ListenerAdapter {
  private static final long MAX_CACHE_SIZE = 1024;
  
  private static final Cache<Message, Site> CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.ofHours(1))
        .maximumSize(MAX_CACHE_SIZE)
        .build();
  
  public static void add(Message message, Site site) {
    CACHE.put(message, site);
  }
  
  @Override
  public void onButtonClick(@NonNull ButtonClickEvent event) {
    @Nullable Site site = CACHE.getIfPresent(event.getMessage());
    
    // Unknown message -> ignore
    if (site == null) {
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
        site.moveLeft(embed -> event.getInteraction()
              .deferEdit()
              .setEmbeds(embed)
              .complete());
        break;
      case "right":
        site.moveRight(embed -> event.getInteraction()
              .deferEdit()
              .setEmbeds(embed)
              .complete());
        break;
      default:
        // TODO ERROR
    }
  }
  
  @Override
  public void onSelectionMenu(@NonNull SelectionMenuEvent event) {
    @Nullable Site site = CACHE.getIfPresent(event.getMessage());
  
    // Unknown message -> ignore
    if (site == null) {
      return;
    }
    
    List<String> values = event.getValues();
    
    // Only a single entry can be selected.
    if (values.size() != 1) {
      return;
    }
    
    site.changeSelection(values.get(0), embed -> event.getInteraction()
          .deferEdit()
          .setEmbeds(embed)
          .complete());
  }
}
