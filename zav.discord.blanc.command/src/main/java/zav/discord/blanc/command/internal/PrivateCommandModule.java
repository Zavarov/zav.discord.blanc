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

package zav.discord.blanc.command.internal;

import com.google.inject.AbstractModule;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * A module for injecting all fields of a private command.
 */
@NonNullByDefault
public class PrivateCommandModule extends AbstractModule {
  
  private final Message message;
  
  public PrivateCommandModule(Message message) {
    this.message = message;
  }
  
  @Override
  protected void configure() {
    // General
    bind(JDA.class).toInstance(message.getJDA());
    bind(MessageChannel.class).toInstance(message.getChannel());
    bind(Message.class).toInstance(message);
    bind(User.class).toInstance(message.getAuthor());
    
    // Private channel specific
    bind(PrivateChannel.class).toInstance(message.getPrivateChannel());
  }
}
