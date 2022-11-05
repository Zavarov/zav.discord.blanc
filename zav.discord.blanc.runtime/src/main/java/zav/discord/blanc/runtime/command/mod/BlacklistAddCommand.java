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

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.cache.PatternCache;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.runtime.internal.PersistenceUtils;

/**
 * This command blacklists certain words. Any message that contains the word will be deleted by the
 * application.
 */
public class BlacklistAddCommand extends AbstractGuildCommand {
  private final PatternCache cache;
  private final SlashCommandEvent event;
  private final EntityManagerFactory factory;

  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public BlacklistAddCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager);
    this.event = event;
    this.cache = manager.getClient().getPatternCache();
    this.factory = manager.getClient().getEntityManagerFactory();
  }

  @Override
  public void run() {
    PersistenceUtils.handle(factory, event, this::modify);
  }

  private String modify(EntityManager entityManager, GuildEntity entity) {
    String pattern = Objects.requireNonNull(event.getOption("pattern")).getAsString();
    
    return addByName(entity, pattern);
  }
  
  private String addByName(GuildEntity entity, String pattern) {
    if (!entity.getBlacklist().contains(pattern)) {
      entity.getBlacklist().add(pattern);
      
      cache.invalidate(event.getGuild());
      
      return getMessage("blacklist_add", pattern);
    }
    
    return getMessage("blacklist_already_added", pattern);
  }
  
  @Override
  protected Set<Permission> getPermissions() {
    return EnumSet.of(Permission.MESSAGE_MANAGE);
  }
}
