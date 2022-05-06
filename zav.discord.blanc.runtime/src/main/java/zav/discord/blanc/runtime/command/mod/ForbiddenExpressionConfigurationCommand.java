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

import static org.apache.commons.lang3.StringUtils.LF;

import java.util.List;
import java.util.Optional;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import zav.discord.blanc.api.Site;

public class ForbiddenExpressionConfigurationCommand extends AbstractConfigurationCommand {
  @Override
  protected Optional<Site.Page> createPage() {
    if (guildData.getBlacklist().isEmpty()) {
      return Optional.empty();
    }
    
    String value = guildData.getBlacklist()
          .stream()
          .reduce((u, v) -> u + LF + v)
          .orElse("");
    
    MessageEmbed content = new EmbedBuilder()
          .setTitle("Forbidden Expressions")
          .setDescription(value)
          .build();
    
    Site.Page mainPage = Site.Page.create("Forbidden Expressions", List.of(content));
    return Optional.of(mainPage);
  }
}
