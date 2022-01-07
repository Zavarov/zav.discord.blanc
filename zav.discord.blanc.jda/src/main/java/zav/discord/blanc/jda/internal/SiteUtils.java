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

package zav.discord.blanc.jda.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import zav.discord.blanc.api.site.SiteListener;
import zav.discord.blanc.databind.message.SiteValueObject;

/**
 * Utility class for creating the message components from their data transfer objects.
 */
public final class SiteUtils {
  private SiteUtils() {}
  
  /**
   * Calculates the action rows for the given input. A message can have two rows: A row of buttons
   * for flipping between the different pages of a single site and a row for switching between
   * multiple sites.
   *
   * @param listener The listener which should be notified whenever the user interacts with this
   *                message.
   * @param sites A list of all sites that can be displayed by this message.
   * @return An array over all required action rows.
   */
  public static ActionRow[] getActionRows(SiteListener listener, List<SiteValueObject> sites) {
    List<ActionRow> actionRows = new ArrayList<>(2);
  
    createSelectionMenu(sites).ifPresent(actionRows::add);
    createButtons(listener, sites).ifPresent(actionRows::add);
    
    return actionRows.toArray(ActionRow[]::new);
  }
  
  /**
   * Creates the buttons for flipping between the pages. Those buttons are only created, iff at
   * least one page site contains more than one page.
   *
   * @param listener The listener which should be notified whenever the user interacts with this
   *                message.
   * @param sites A list of all sites that can be displayed by this message.
   * @return An optional containing the required buttons for the site.
   */
  private static Optional<ActionRow> createButtons(SiteListener listener, List<SiteValueObject> sites) {
    int maxPages = sites.stream()
          .map(SiteValueObject::getPages)
          .mapToInt(List::size)
          .max()
          .orElse(0);
    
    if (maxPages > 1) {
      return Optional.empty();
    }
    
    Button left = Button.primary("left", Emoji.fromUnicode("⬅️"))
          .withDisabled(!listener.canMoveLeft());
    
    Button right = Button.primary("right", Emoji.fromUnicode("➡️"))
          .withDisabled(!listener.canMoveRight());
    
    return Optional.of(ActionRow.of(left, right));
  }
  
  private static Optional<ActionRow> createSelectionMenu(List<SiteValueObject> sites) {
    if (sites.size() > 1) {
      return Optional.empty();
    }
    
    SelectionMenu.Builder builder = SelectionMenu.create("menuSelection");
    
    for (SiteValueObject site : sites) {
      builder.addOption(site.getLabel(), site.getLabel(), site.getDescription());
    }
    SelectionMenu selectionMenu = builder.build();
    
    return Optional.of(ActionRow.of(selectionMenu));
  }
}
