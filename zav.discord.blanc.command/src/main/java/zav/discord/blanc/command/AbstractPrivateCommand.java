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

package zav.discord.blanc.command;

import javax.inject.Inject;
import net.dv8tion.jda.api.entities.PrivateChannel;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.api.Rank;

/**
 * Base class for all private commands.<br>
 */
@NonNullByDefault
public abstract class AbstractPrivateCommand extends AbstractCommand {
  @Inject
  protected PrivateChannel channel;
  
  protected AbstractPrivateCommand(Rank rank) {
    super(rank);
  }
  
  protected AbstractPrivateCommand() {
    super();
  }
}
