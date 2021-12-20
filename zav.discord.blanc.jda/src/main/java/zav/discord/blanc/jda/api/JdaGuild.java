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

import com.google.inject.Injector;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.activity.ActivityChart;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.databind.GuildValueObject;
import zav.discord.blanc.databind.RoleValueObject;
import zav.discord.blanc.databind.TextChannelValueObject;
import zav.discord.blanc.databind.activity.DataPointValueObject;
import zav.discord.blanc.jda.internal.GuiceUtils;
import zav.discord.blanc.jda.internal.listener.BlacklistListener;
import zav.discord.blanc.jda.internal.listener.GuildActivityListener;

/**
 * Implementation of a guild view, backed by JDA.
 */
public class JdaGuild implements zav.discord.blanc.api.Guild {
  protected final ActivityChart activityChart;
  
  @Inject
  private Injector injector;
  
  @Inject
  protected Guild jdaGuild;
  
  public JdaGuild() {
    this.activityChart = new ActivityChart();
  }
  
  @Override
  public GuildValueObject getAbout() {
    return aboutGuild(jdaGuild);
  }
  
  @Override
  public JdaSelfMember getSelfMember() {
    Member jdaSelfMember = jdaGuild.getSelfMember();
    
    return injectSelfMember(injector, jdaSelfMember);
  }
  
  @Override
  public JdaRole getRole(Argument argument) throws NoSuchElementException {
    Role jdaRole = resolveRole(jdaGuild, argument);
    
    return injectRole(injector, jdaRole);
  }
  
  @Override
  public JdaMember getMember(Argument argument) throws NoSuchElementException {
    Member jdaMember = resolveMember(jdaGuild, argument);
  
    return injectMember(injector, jdaMember);
  }
  
  @Override
  public JdaTextChannel getTextChannel(Argument argument) {
    TextChannel jdaTextChannel = resolveTextChannel(jdaGuild, argument);
  
    return injectTextChannel(injector, jdaTextChannel);
  }
  
  @Override
  public Collection<JdaRole> getRoles() {
    return jdaGuild.getRoles()
          .stream()
          .map(role -> injectRole(injector, role))
          .collect(Collectors.toUnmodifiableList());
  }
  
  @Override
  public Collection<JdaMember> getMembers() {
    return jdaGuild.getMembers()
          .stream()
          .map(member -> injectMember(injector, member))
          .collect(Collectors.toUnmodifiableList());
  }
  
  @Override
  public Collection<JdaTextChannel> getTextChannels() {
    return jdaGuild.getTextChannels()
          .stream()
          .map(textChannel -> injectTextChannel(injector, textChannel))
          .collect(Collectors.toUnmodifiableList());
  }
  
  @Override
  public void updateActivity() {
    DataPointValueObject dp = new DataPointValueObject();
    
    double activity = GuildActivityListener.getActivity(this);
    long membersCount = jdaGuild.getMemberCount();
    long membersOnline = jdaGuild.getMembers()
          .stream()
          .filter(member -> member.getOnlineStatus() == OnlineStatus.ONLINE)
          .count();
    
    dp.setActivity(activity);
    dp.setMembersCount(membersCount);
    dp.setMembersOnline(membersOnline);
    
    activityChart.add(dp);
  }
  
  @Override
  public void updateBlacklist(Pattern pattern) {
    BlacklistListener.setPattern(jdaGuild.getIdLong(), pattern);
  }
  
  @Override
  public void leave() {
    jdaGuild.leave().complete();
  }
  
  @Override
  public boolean canInteract(zav.discord.blanc.api.Member member, RoleValueObject role) {
    @Nullable Member jdaMember = jdaGuild.getMemberById(member.getAbout().getId());
    @Nullable Role jdaRole = jdaGuild.getRoleById(role.getId());
  
    // Return false, if either the member or the role doesn't exist.
    return jdaMember != null && jdaRole != null && jdaMember.canInteract(jdaRole);
  }
  
  @Override
  public BufferedImage getActivity(List<TextChannelValueObject> channels) {
    return activityChart.new Builder()
          .withGuild(this)
          .withChannels(channels)
          .build(new Rectangle(800, 600));
  }
}
