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

import static net.dv8tion.jda.api.Permission.MESSAGE_MANAGE;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.runtime.internal.PageUtils;

/**
 * This command allows to ban certain expressions in a guild. Every message that matches at least
 * one of those banned expressions is deleted automatically.
 */
public class BlacklistInfoCommand extends AbstractGuildCommand {
  
  private final EntityManagerFactory factory;
  private final GuildCommandManager manager;
  private final Client client;
  private final Guild guild;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  @Inject
  public BlacklistInfoCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager, MESSAGE_MANAGE);
    this.guild = event.getGuild();
    this.manager = manager;
    this.client = manager.getClient();
    this.factory = client.getEntityManagerFactory();
  }

  @Override
  public void run() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity entity = GuildEntity.getOrCreate(entityManager, guild);

      List<Site.Page> pages = PageUtils.convert("Forbidden Expressions", entity.getBlacklist(), 10);

      manager.submit(pages);
    }
  }
}
