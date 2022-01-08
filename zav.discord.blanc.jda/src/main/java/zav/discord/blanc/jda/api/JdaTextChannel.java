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

import static zav.discord.blanc.jda.internal.DatabaseUtils.aboutTextChannel;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectGuildMessage;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectWebHook;
import static zav.discord.blanc.jda.internal.MessageUtils.forMember;
import static zav.discord.blanc.jda.internal.ResolverUtils.resolveMessage;

import java.util.List;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.databind.TextChannelDto;
import zav.discord.blanc.databind.UserDto;

/**
 * Implementation of a text channel view, backed by JDA.
 */
public class JdaTextChannel extends JdaMessageChannel implements zav.discord.blanc.api.TextChannel {
  private static final Logger LOGGER = LogManager.getLogger(JdaTextChannel.class);
  
  @Inject
  protected TextChannel jdaTextChannel;
  
  @Override
  public TextChannelDto getAbout() {
    return aboutTextChannel(jdaTextChannel);
  }
  
  @Override
  public JdaGuildMessage getMessage(Argument argument) {
    Message jdaMessage = resolveMessage(jdaTextChannel, argument);

    return injectGuildMessage(jdaMessage);
  }
  
  @Override
  public JdaWebHook getWebHook(String argument, boolean create) {
    List<Webhook> jdaWebhooks = jdaTextChannel.retrieveWebhooks().complete();
    
    // WebHook already exists -> reuse
    for (Webhook jdaWebHook : jdaWebhooks) {
      if (jdaWebHook.getName().equals(argument)) {
        return injectWebHook(jdaWebHook);
      }
    }
    
    // Otherwise create one, if desired
    if (create) {
      Webhook jdaWebHook = jdaTextChannel.createWebhook(argument).complete();
  
      return injectWebHook(jdaWebHook);
    // No matching webhook exists -> error
    } else {
      LOGGER.error("No matching webhook for {} has been found.", argument);
      throw new RuntimeException();
    }
  }
  
  @Override
  public void send(UserDto user) {
    @Nullable Member jdaMember = jdaTextChannel.getGuild().getMemberById(user.getId());
    
    // In case someone wants to show information about a user who isn't in this guild
    if (jdaMember == null) {
      super.send(user);
    } else {
      jdaMessageChannel.sendMessageEmbeds(forMember(jdaMember)).complete();
    }
  }
  
  @Override
  public String getAsMention() {
    return jdaTextChannel.getAsMention();
  }
}
