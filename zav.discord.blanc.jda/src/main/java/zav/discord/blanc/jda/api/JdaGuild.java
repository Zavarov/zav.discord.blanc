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

import static zav.discord.blanc.jda.internal.DatabaseUtils.aboutGuild;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectMember;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectRole;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectSelfMember;
import static zav.discord.blanc.jda.internal.GuiceUtils.injectTextChannel;
import static zav.discord.blanc.jda.internal.ResolverUtils.resolveMember;
import static zav.discord.blanc.jda.internal.ResolverUtils.resolveRole;
import static zav.discord.blanc.jda.internal.ResolverUtils.resolveTextChannel;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.databind.GuildDto;
import zav.discord.blanc.jda.internal.GuiceUtils;
import zav.discord.blanc.jda.internal.listener.BlacklistListener;

/**
 * Implementation of a guild view, backed by JDA.
 */
public class JdaGuild implements zav.discord.blanc.api.Guild {
  
  @Inject
  protected Guild jdaGuild;
  
  @Override
  public GuildDto getAbout() {
    return aboutGuild(jdaGuild);
  }
  
  @Override
  public JdaSelfMember getSelfMember() {
    Member jdaSelfMember = jdaGuild.getSelfMember();
    
    return injectSelfMember(jdaSelfMember);
  }
  
  @Override
  public JdaRole getRole(Argument argument) throws NoSuchElementException {
    Role jdaRole = resolveRole(jdaGuild, argument);
    
    return injectRole(jdaRole);
  }
  
  @Override
  public JdaMember getMember(Argument argument) throws NoSuchElementException {
    Member jdaMember = resolveMember(jdaGuild, argument);
  
    return injectMember(jdaMember);
  }
  
  @Override
  public JdaTextChannel getTextChannel(Argument argument) {
    TextChannel jdaTextChannel = resolveTextChannel(jdaGuild, argument);
  
    return injectTextChannel(jdaTextChannel);
  }
  
  @Override
  public Collection<JdaRole> getRoles() {
    return jdaGuild.getRoles()
          .stream()
          .map(GuiceUtils::injectRole)
          .collect(Collectors.toUnmodifiableList());
  }
  
  @Override
  public Collection<JdaMember> getMembers() {
    return jdaGuild.getMembers()
          .stream()
          .map(GuiceUtils::injectMember)
          .collect(Collectors.toUnmodifiableList());
  }
  
  @Override
  public Collection<JdaTextChannel> getTextChannels() {
    return jdaGuild.getTextChannels()
          .stream()
          .map(GuiceUtils::injectTextChannel)
          .collect(Collectors.toUnmodifiableList());
  }
  
  @Override
  public void updateBlacklist(Pattern pattern) {
    BlacklistListener.setPattern(jdaGuild.getIdLong(), pattern);
  }
  
  @Override
  public void leave() {
    jdaGuild.leave().complete();
  }
}
