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

import static zav.discord.blanc.jda.internal.GuiceUtils.injectGuild;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectMember;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectTextChannel;

import java.util.Objects;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import zav.discord.blanc.api.GuildMessage;

/**
 * Implementation of a guild message view, backed by JDA.
 */
public class JdaGuildMessage extends JdaMessage implements GuildMessage {
  
  @Override
  public JdaMember getAuthor() {
    Member jdaMember = Objects.requireNonNull(jdaMessage.getMember());
    
    return injectMember(jdaMember);
  }
  
  @Override
  public JdaGuild getGuild() {
    Guild jdaGuild = jdaMessage.getGuild();
  
    return injectGuild(jdaGuild);
  }
  
  @Override
  public JdaTextChannel getMessageChannel() {
    TextChannel jdaTextChannel = jdaMessage.getTextChannel();
  
    return injectTextChannel(jdaTextChannel);
  }
}
