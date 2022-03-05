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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import zav.discord.blanc.api.Parameter;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.internal.resolver.BigDecimalResolver;
import zav.discord.blanc.command.internal.resolver.EntityResolver;
import zav.discord.blanc.command.internal.resolver.GuildResolver;
import zav.discord.blanc.command.internal.resolver.MemberResolver;
import zav.discord.blanc.command.internal.resolver.RankResolver;
import zav.discord.blanc.command.internal.resolver.RoleResolver;
import zav.discord.blanc.command.internal.resolver.StringResolver;
import zav.discord.blanc.command.internal.resolver.TextChannelResolver;
import zav.discord.blanc.command.internal.resolver.UserResolver;

/**
 * Utility class for transforming parameters into objects.
 */
@NonNullByDefault
public final class ParameterResolver {
  
  private ParameterResolver() {
  }
  
  private static final Map<Class<?>, EntityResolver<?>> RESOLVERS = new HashMap<>();
  
  static {
    RESOLVERS.put(BigDecimal.class, new BigDecimalResolver());
    RESOLVERS.put(Guild.class, new GuildResolver());
    RESOLVERS.put(Member.class, new MemberResolver());
    RESOLVERS.put(Rank.class, new RankResolver());
    RESOLVERS.put(Role.class, new RoleResolver());
    RESOLVERS.put(String.class, new StringResolver());
    RESOLVERS.put(TextChannel.class, new TextChannelResolver());
    RESOLVERS.put(User.class, new UserResolver());
  }
  
  /**
   * Transforms the parameter in the requested type. Additional JDA entities may be retrieved from
   * the provided message.
   *
   * @param clazz The requested type.
   * @param source A command parameter.
   * @param message The message corresponding causing the current command creation.
   * @return An instance of the requested type or {@code null}, if none exist.
   */
  public static @Nullable Object resolve(Class<?> clazz, Parameter source, Message message) {
    EntityResolver<?> resolver = RESOLVERS.get(clazz);
    
    if (resolver == null) {
      return null;
    }
    
    return resolver.apply(source, message);
  }
  
  private static final Map<Class<?>, Function<Message, ?>> DEFAULTS = new HashMap<>();
  
  static {
    DEFAULTS.put(JDA.class, Message::getJDA);
    DEFAULTS.put(Guild.class, Message::getGuild);
    DEFAULTS.put(TextChannel.class, Message::getTextChannel);
    DEFAULTS.put(Member.class, Message::getMember);
    DEFAULTS.put(User.class, Message::getAuthor);
  }
  
  /**
   * Extracts an object of the requested type of the given message. E.g. the {@link JDA} of the
   * current shard is retrieved via {@link Message#getJDA()}.
   *
   * @param clazz The requested type.
   * @param source The message corresponding causing the current command creation.
   * @return An instance of the requested type or {@code null}, if none exist.
   */
  public static @Nullable Object getDefault(Class<?> clazz, Message source) {
    Function<Message, ?> mapper = DEFAULTS.get(clazz);
  
    if (mapper == null) {
      return null;
    }
  
    return mapper.apply(source);
  }
}
