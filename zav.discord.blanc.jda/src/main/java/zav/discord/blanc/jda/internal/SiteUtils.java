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

import net.dv8tion.jda.api.entities.Emoji;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectionMenu;
import zav.discord.blanc.api.site.SiteListener;
import zav.discord.blanc.databind.message.SiteValueObject;

import java.util.ArrayList;
import java.util.List;

public final class SiteUtils {
  private SiteUtils() {}
  public static ActionRow[] getActionRows(SiteListener listener, List<SiteValueObject> sites) {
    List<ActionRow> actionRows = new ArrayList<>(2);
    
    actionRows.add(createButtons(listener));
    actionRows.add(createSelectionMenu(sites));
    
    return actionRows.toArray(ActionRow[]::new);
  }
  
  private static ActionRow createButtons(SiteListener listener) {
    Button left = Button.primary("left", Emoji.fromUnicode("⬅️"))
          .withDisabled(listener.canMoveLeft());
    
    Button right = Button.primary("right", Emoji.fromUnicode("➡️"))
          .withDisabled(listener.canMoveRight());
    
    return ActionRow.of(left, right);
  }
  
  private static ActionRow createSelectionMenu(List<SiteValueObject> sites) {
    SelectionMenu.Builder builder = SelectionMenu.create("menuSelection");
    
    for (SiteValueObject site : sites) {
      builder.addOption(site.getLabel(), site.getLabel(), site.getDescription());
    }
    SelectionMenu selectionMenu = builder.build();
    
    return ActionRow.of(selectionMenu);
  }
}
