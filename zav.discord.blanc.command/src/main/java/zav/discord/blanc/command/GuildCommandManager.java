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

package zav.discord.blanc.command;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.ButtonStyle;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.api.cache.SiteCache;
import zav.discord.blanc.command.internal.PermissionValidator;

/**
 * The command manager is a utility class containing all methods which are required by one or more
 * guild commands.
 */
@NonNullByDefault
public class GuildCommandManager extends CommandManager {
  private static final Button LEFT = Button.of(ButtonStyle.PRIMARY, "left", "←");
  private static final Button RIGHT = Button.of(ButtonStyle.PRIMARY, "right", "→");
  private final TextChannel textChannel;
  private final Member member;
  private final PermissionValidator validator;

  /**
   * Creates a new manager instance. A new instance is created for each command.
   *
   * @param client The Discord client.
   * @param event The event from which the active command was created.
   */
  public GuildCommandManager(Client client, SlashCommandEvent event) {
    super(client, event);
    this.textChannel = event.getTextChannel();
    this.member = Objects.requireNonNull(event.getMember());
    this.validator = new PermissionValidator(member, textChannel);
  }
  
  /**
   * Checks whether the author of the command has the given permissions.
   *
   * @param permissions A guild-specific collection of permissions.
   * @throws InsufficientPermissionException If the user lacks at least one of the given
   *                                         permissions.
   */
  public void validate(Collection<Permission> permissions) throws InsufficientPermissionException {
    validator.validate(permissions);
  }
  
  /**
   * Submits the interactive message. If the message contains no pages, a quick notification is
   * submitted. Otherwise, the main page is submitted and the corresponding message stored into the
   * cache, such that this program can respond to any further user interactions.
   *
   * @param pages All pages of the interactive message.
   */
  public void submit(List<Site.Page> pages) {
    if (pages.isEmpty()) {
      event.reply("No entries.").complete();
    } else {
      SiteCache cache = client.get(SiteCache.class);
      
      // Build site
      Site site = Site.create(pages, event.getUser());
      
      // Send response
      ReplyAction action = event.deferReply();
      
      // Only add the left & right arrows if necessary
      if (site.getCurrentSize() > 1) {
        action = action.addActionRows(ActionRow.of(LEFT, RIGHT));
      }
      
      action = action.addEmbeds(site.getCurrentPage());
      
      InteractionHook response = action.complete();
      
      // Store message in cache
      Message source = response.retrieveOriginal().complete();
      cache.put(source, site);
    }
  }
}
