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
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import org.apache.commons.lang3.StringUtils;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;

/**
 * This command allows to ban certain expressions in a guild. Every message that matches at least
 * one of those banned expressions is deleted automatically.
 */
public class BlacklistConfigurationCommand extends AbstractGuildCommand {
  
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
  public BlacklistConfigurationCommand(SlashCommandEvent event, GuildCommandManager manager) {
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
      List<Site.Page> pages = new ArrayList<>();
      
      String value = entity.getBlacklist()
          .stream()
          .reduce((u, v) -> u + StringUtils.LF + v)
          .orElse(null);
      
      // Skip, if the guild doesn't contain any banned words
      if (value != null) {
        MessageEmbed content = new EmbedBuilder()
              .setTitle("Forbidden Expressions")
              .setDescription(MarkdownSanitizer.escape(value))
              .build();
        
        Site.Page mainPage = Site.Page.create("Forbidden Expressions", List.of(content));
        
        pages.add(mainPage);
      }
      
      manager.submit(pages);
    }
  }
}
