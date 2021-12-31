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

import static zav.discord.blanc.jda.internal.ImageUtils.asInputStream;
import static zav.discord.blanc.jda.internal.MessageUtils.forGuild;
import static zav.discord.blanc.jda.internal.MessageUtils.forLink;
import static zav.discord.blanc.jda.internal.MessageUtils.forRole;
import static zav.discord.blanc.jda.internal.MessageUtils.forSite;
import static zav.discord.blanc.jda.internal.MessageUtils.forUser;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.site.SiteListener;
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.databind.RoleValueObject;
import zav.discord.blanc.databind.UserValueObject;
import zav.discord.blanc.databind.message.SiteValueObject;
import zav.discord.blanc.jda.internal.listener.SiteComponentListener;
import zav.jrc.databind.LinkValueObject;

/**
 * Implementation of a message channel view, backed by JDA.
 */
public abstract class JdaMessageChannel implements zav.discord.blanc.api.MessageChannel {
  private static final Logger LOGGER = LogManager.getLogger(JdaMessageChannel.class);
  
  @Inject
  protected MessageChannel jdaMessageChannel;
  
  @Override
  public void send(BufferedImage image, String imageName) {
    try {
      jdaMessageChannel.sendFile(asInputStream(image), imageName).complete();
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
  
  @Override
  public void send(Object content) {
    jdaMessageChannel.sendMessage(content.toString()).complete();
  }
  
  @Override
  public void send(GuildValueObject guild) {
    @Nullable Guild jdaGuild = jdaMessageChannel.getJDA().getGuildById(guild.getId());
  
    // Guild must exist!
    Objects.requireNonNull(jdaGuild);
    
    jdaMessageChannel.sendMessageEmbeds(forGuild(jdaGuild)).complete();
  }
  
  @Override
  public void send(UserValueObject user) {
    @Nullable User jdaUser = jdaMessageChannel.getJDA().getUserById(user.getId());
  
    // User must exist!
    Objects.requireNonNull(jdaUser);
  
    jdaMessageChannel.sendMessageEmbeds(forUser(jdaUser)).complete();
  }
  
  @Override
  public void send(RoleValueObject role) {
    @Nullable Role jdaRole = jdaMessageChannel.getJDA().getRoleById(role.getId());
    
    // Role must exist!
    Objects.requireNonNull(jdaRole);
  
    jdaMessageChannel.sendMessageEmbeds(forRole(jdaRole)).complete();
  }
  
  @Override
  public void send(LinkValueObject link) {
    jdaMessageChannel.sendMessage(forLink(link)).complete();
  }
  
  @Override
  public void send(SiteListener listener, List<SiteValueObject> sites) {
    // Register the listener for this component
    jdaMessageChannel.sendMessage(forSite(listener, sites)).queue(response -> SiteComponentListener.add(response, listener));
  }
}
