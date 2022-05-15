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
import static zav.discord.blanc.api.Constants.SITE;
import static zav.discord.blanc.runtime.internal.DatabaseUtils.getOrCreate;

import com.google.common.cache.Cache;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.inject.Named;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Site;
import zav.discord.blanc.command.AbstractGuildCommand;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.db.GuildTable;

/**
 * Displays the guild-specific configuration like subreddit feeds.
 */
public abstract class AbstractConfigurationCommand extends AbstractGuildCommand {
  @Inject
  @Named(SITE)
  protected Cache<Long, Site> cache;
  
  @Inject
  protected GuildTable db;
  
  @Inject
  protected SlashCommandEvent event;
  
  @Inject
  protected Guild guild;
  
  @Inject
  protected User author;
  
  protected GuildEntity guildData;
  
  public AbstractConfigurationCommand() {
    super(MESSAGE_MANAGE);
  }
  
  @Override
  public void postConstruct() {
    guildData = getOrCreate(db, guild);
  }
  
  @Override
  public void run() throws SQLException {
    List<Site.Page> pages = createPage().stream().collect(Collectors.toUnmodifiableList());
    
    if (pages.isEmpty()) {
      event.reply("No entries").complete();
    } else {
      // Build site
      Site site = Site.create(pages, author);
      
      // Send response
      event.replyEmbeds(site.getCurrentPage()).queue(success -> cache.put(success.retrieveOriginal().complete().getIdLong(), site));
    }
  }
  
  protected abstract Optional<Site.Page> createPage() throws SQLException;
}
