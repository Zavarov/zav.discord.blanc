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

import static zav.discord.blanc.jda.internal.GuiceUtils.injectPrivateMessage;
import static zav.discord.blanc.jda.internal.ResolverUtils.resolveMessage;

import net.dv8tion.jda.api.entities.Message;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.PrivateChannel;


/**
 * Implementation of a message view, backed by JDA.
 */
public class JdaPrivateChannel extends JdaMessageChannel implements PrivateChannel {
  
  @Override
  public JdaPrivateMessage getMessage(Argument argument) {
    Message jdaMessage = resolveMessage(jdaMessageChannel, argument);
    
    return injectPrivateMessage(jdaMessage);
  }
}
