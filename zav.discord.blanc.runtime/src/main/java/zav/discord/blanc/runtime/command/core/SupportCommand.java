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

package zav.discord.blanc.runtime.command.core;

import static zav.discord.blanc.api.Constants.INVITE_SUPPORT_SERVER;

import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.command.AbstractCommand;

/**
 * This command prints an invitation link to the support server.
 */
@NonNullByDefault
public class SupportCommand extends AbstractCommand {
  @Inject
  @Named(INVITE_SUPPORT_SERVER)
  private String link;
  
  @Override
  public void run() {
    event.replyFormat(i18n.getString("server_invitation"), link).complete();
  }
}
