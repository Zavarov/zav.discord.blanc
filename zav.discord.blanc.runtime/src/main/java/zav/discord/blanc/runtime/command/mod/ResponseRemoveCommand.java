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
import java.util.Set;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.cache.AutoResponseCache;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.AutoResponseEntity;
import zav.discord.blanc.databind.GuildEntity;

/**
 * This command allows the user to remove automatic responses. The responses are identified by their
 * (fixed) index in the database.
 */
public class ResponseRemoveCommand extends AbstractGuildCommand {
  private final AutoResponseCache cache;
  private final SlashCommandEvent event;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public ResponseRemoveCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager);
    this.event = event;
    this.cache = manager.getShard().get(AutoResponseCache.class);
  }

  @Override
  public void run() {
    GuildEntity entity = GuildEntity.find(event.getGuild());
    event.reply(modify(entity)).complete();
    entity.merge();
  }

  private String modify(GuildEntity entity) {
    int index = (int) event.getOption("index").getAsLong();
    
    if (index < 0 || index >= entity.getAutoResponses().size()) {
      return getMessage("response_index_not_found", index);
    }
    
    AutoResponseEntity responseEntity = entity.getAutoResponses().get(index);
    entity.remove(responseEntity);
    
    // Remove the corresponding entry from cache
    cache.invalidate(event.getGuild());
    
    return getMessage("response_removed", responseEntity.getPattern());
  }
  
  @Override
  protected Set<Permission> getPermissions() {
    return EnumSet.of(Permission.MESSAGE_MANAGE);
  }

}
