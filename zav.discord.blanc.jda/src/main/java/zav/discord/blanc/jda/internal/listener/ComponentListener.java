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

package zav.discord.blanc.jda.internal.listener;

import static zav.discord.blanc.jda.internal.MessageEmbedUtils.forPage;

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
import zav.discord.blanc.api.site.SiteListener;

public class ComponentListener extends ListenerAdapter {
  private static final long MAX_CACHE_SIZE = 1024;
  
  private static final Cache<Message, SiteListener> CACHE = CacheBuilder.newBuilder()
        .expireAfterAccess(Duration.ofHours(1))
        .maximumSize(MAX_CACHE_SIZE)
        .build();
  
  public static void add(Message jdaMessage, SiteListener listener) {
    CACHE.put(jdaMessage, listener);
  }
  
  @Override
  public void onButtonClick(@NonNull ButtonClickEvent event) {
    @Nullable SiteListener listener = CACHE.getIfPresent(event.getMessage());
    
    // Unknown message -> ignore
    if (listener == null) {
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
    
    Message jdaMessage = event.getMessage();
    
    switch (id) {
      case "left":
        listener.moveLeft(embed -> jdaMessage.editMessageEmbeds(forPage(embed)).complete());
        break;
      case "right":
        listener.moveRight(embed -> jdaMessage.editMessageEmbeds(forPage(embed)).complete());
        break;
      default:
      
    }
    event.getButton();
    super.onButtonClick(event);
  }
  
  @Override
  public void onSelectionMenu(@NonNull SelectionMenuEvent event) {
    @Nullable SiteListener listener = CACHE.getIfPresent(event.getMessage());
  
    // Unknown message -> ignore
    if (listener == null) {
      return;
    }
    
    List<String> values = event.getValues();
    
    // Only a single entry can be selected.
    if (values.size() != 1) {
      return;
    }
    
    listener.changeSelection(values.get(0));
  }
}
