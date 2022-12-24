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

package zav.discord.blanc.runtime.command.mod;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.AutoResponseEntity;
import zav.discord.blanc.databind.GuildEntity;

/**
 * This command displays all currently registered auto-responses.
 */
public class ResponseInfoCommand extends AbstractGuildCommand {
  
  private final GuildCommandManager manager;
  private final SlashCommandEvent event;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public ResponseInfoCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager);
    this.manager = manager;
    this.event = event;
  }

  @Override
  public void run() {
    manager.submit(getPages(), "Automatic Responses");
  }
  
  private List<Site.Page> getPages() {
    Site.Page.Builder builder = new Site.Page.Builder("Subreddit Feeds");
    builder.setItemsPerPage(5);
    
    GuildEntity entity = GuildEntity.find(event.getGuild());

    List<AutoResponseEntity> responses = entity.getAutoResponses();
    for (int i = 0; i < responses.size(); ++i) {
      String pattern = MarkdownSanitizer.escape(responses.get(i).getPattern());
      String answer = MarkdownSanitizer.escape(responses.get(i).getAnswer());
      builder.add("`[{0}]` {1}\n → _{2}_\n", i, pattern, answer);
    }

    return builder.build();
  }
  
  @Override
  protected Set<Permission> getPermissions() {
    return EnumSet.of(Permission.MESSAGE_MANAGE);
  }
}
