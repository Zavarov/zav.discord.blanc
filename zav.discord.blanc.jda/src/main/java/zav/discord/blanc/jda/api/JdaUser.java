/*
 * Copyright (c) 2021 Zavarov.
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

package zav.discord.blanc.jda.api;

import static zav.discord.blanc.jda.internal.DatabaseUtils.aboutUser;

import javax.inject.Inject;
import net.dv8tion.jda.api.entities.User;
import zav.discord.blanc.databind.UserValueObject;

/**
 * Implementation of a user view, backed by JDA.
 */
public class JdaUser implements zav.discord.blanc.api.User {
  @Inject
  protected User jdaUser;
  
  @Override
  public UserValueObject getAbout() {
    return aboutUser(jdaUser);
  }
  
  @Override
  public String getAsMention() {
    return jdaUser.getAsMention();
  }
}
