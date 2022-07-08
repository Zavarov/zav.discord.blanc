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

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.PatternCache;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.command.GuildCommandManager;
import zav.discord.blanc.databind.GuildEntity;

/**
 * This command blacklists certain words. Any message that contains the word will be deleted by the
 * application.
 */
public class BlacklistCommand extends AbstractGuildCommand {
  private final EntityManagerFactory factory;
  private final SlashCommandEvent event;
  private final ResourceBundle i18n;
  private final PatternCache cache;
  private final Client client;
  private final Guild guild;
  private final String regex;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  @Inject
  public BlacklistCommand(SlashCommandEvent event, GuildCommandManager manager) {
    super(manager, MESSAGE_MANAGE);
    this.event = event;
    this.guild = event.getGuild();
    this.regex = Objects.requireNonNull(event.getOption("regex")).getAsString();
    this.i18n = manager.getResourceBundle();
    this.client = manager.getClient();
    this.cache = client.getPatternCache();
    this.factory = client.getEntityManagerFactory();
  }
  
  @Override
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  public void run() {    
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity entity = GuildEntity.getOrCreate(entityManager, guild);
      String response;
      
      if (entity.getBlacklist().contains(regex)) {
        entity.getBlacklist().remove(regex);
        response = i18n.getString("remove_blacklist");
      } else {
        entity.getBlacklist().add(regex);
        response = i18n.getString("add_blacklist");
      }
      
      entityManager.getTransaction().begin();
      entityManager.merge(entity);
      entityManager.getTransaction().commit();
      
      cache.invalidate(guild);
      event.replyFormat(response, regex).complete();
    }
  }
}
