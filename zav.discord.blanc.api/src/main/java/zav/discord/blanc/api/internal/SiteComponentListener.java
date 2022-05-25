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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Named;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SelectionMenuEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.Contract;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Site;

/**
 * The listener for notifying the message components of a command whenever the author interacts with
 * it.
 */
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "That's the point...")
public class SiteComponentListener extends ListenerAdapter {
  private static final Logger LOGGER = LoggerFactory.getLogger(SiteComponentListener.class);
  
  private final Cache<Long, Site> siteCache;
  
  @Inject
  public SiteComponentListener(@Named(SITE) Cache<Long, Site> siteCache) {
    this.siteCache = siteCache;
  }
  
  @Override
  @Contract(mutates = "this")
  public void onButtonClick(ButtonClickEvent event) {
    @Nullable Site site = siteCache.getIfPresent(event.getMessage().getIdLong());
    
    // Unknown message -> ignore
    if (site == null) {
      event.reply("Invalid Message").setEphemeral(true).queue();
      return;
    }
    
    // Invalid user -> reject
    if (!site.getOwner().equals(event.getUser())) {
      event.reply("Invalid User").setEphemeral(true).queue();
      return;
    }
    
    @Nullable Button button = event.getButton();
    
    // Null for ephemeral messages
    if (button == null) {
      event.reply("Ephemeral Message").setEphemeral(true).queue();
      return;
    }
    
    @Nullable String id = button.getId();
    
    // Null if absent
    if (id == null) {
      event.reply("Unknown button").setEphemeral(true).queue();
      return;
    }
    
    switch (id) {
      case "left":
        site.moveLeft();
        event.getInteraction().replyEmbeds(site.getCurrentPage()).queue();
        break;
      case "right":
        site.moveRight();
        event.getInteraction().replyEmbeds(site.getCurrentPage()).queue();
        break;
      default:
        LOGGER.error("unknown id '{}'.", id);
    }
  }
  
  @Override
  @Contract(mutates = "this")
  public void onSelectionMenu(SelectionMenuEvent event) {
    @Nullable Site site = siteCache.getIfPresent(event.getMessage().getIdLong());
  
    // Unknown message -> ignore
    if (site == null) {
      event.reply("Invalid Message").setEphemeral(true).queue();
      return;
    }
  
    // Invalid user -> reject
    if (!site.getOwner().equals(event.getUser())) {
      event.reply("Invalid user").setEphemeral(true).queue();
      return;
    }
    
    List<String> values = event.getValues();
    
    // Only a single entry can be selected.
    if (values.size() != 1) {
      event.reply("Only one entry can be selected").setEphemeral(true).queue();
      return;
    }
    
    site.changeSelection(values.get(0));
    event.replyEmbeds(site.getCurrentPage()).complete();
  }
}
