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
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;

/**
 * This command allows to ban certain expressions in a guild. Every message that matches at least
 * one of those banned expressions is deleted automatically.
 */
public class BlacklistInfoCommand extends AbstractGuildCommand {
  
  private final GuildCommandManager manager;
  private final Guild guild;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public BlacklistInfoCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager);
    this.guild = event.getGuild();
    this.manager = manager;
  }

  @Override
  public void run() {
    manager.submit(getPages(), "Forbidden Expressions");
  }
  
  private List<Site.Page> getPages() {
    Site.Page.Builder builder = new Site.Page.Builder();
    builder.setItemsPerPage(10);
    
    GuildEntity entity = GuildEntity.find(guild);
    
    List<String> patterns = entity.getBlacklist();
    for (int i = 0; i < patterns.size(); ++i) {
      builder.add("`[{0}]` {1}\n", i, MarkdownSanitizer.escape(patterns.get(i)));
    }

    return builder.build();
  }
  
  @Override
  protected Set<Permission> getPermissions() {
    return EnumSet.of(Permission.MESSAGE_MANAGE);
  }
}
