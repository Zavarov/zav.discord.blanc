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

package zav.discord.blanc.jda.internal;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.external.JDAWebhookClient;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import zav.discord.blanc.jda.api.JdaGuild;
import zav.discord.blanc.jda.api.JdaGuildMessage;
import zav.discord.blanc.jda.api.JdaMember;
import zav.discord.blanc.jda.api.JdaPrivateChannel;
import zav.discord.blanc.jda.api.JdaPrivateMessage;
import zav.discord.blanc.jda.api.JdaRole;
import zav.discord.blanc.jda.api.JdaSelfMember;
import zav.discord.blanc.jda.api.JdaSelfUser;
import zav.discord.blanc.jda.api.JdaShard;
import zav.discord.blanc.jda.api.JdaTextChannel;
import zav.discord.blanc.jda.api.JdaUser;
import zav.discord.blanc.jda.api.JdaWebHook;

/**
 * Utility class for instantiating all of the API implementations using Guice.
 */
public final class GuiceUtils {
  /**
   * Use a single executor service for all processes within a shard.
   */
  private static final LoadingCache<JDA, ScheduledExecutorService> queue = CacheBuilder.newBuilder()
        .build(CacheLoader.from(jda -> Executors.newScheduledThreadPool(16)));
  
  private static <T> AbstractModule singletonModule(Class<T> source, T value) {
    return new AbstractModule() {
      @Override
      protected void configure() {
        bind(source).toInstance(value);
      }
    };
  }
  
  private static <T> Injector singletonInjector(Class<T> source, T value) {
    return Guice.createInjector(singletonModule(source, value));
  }
  
  public static JdaGuild injectGuild(Guild guild) {
    return singletonInjector(Guild.class, guild).getInstance(JdaGuild.class);
  }
  
  public static JdaGuildMessage injectGuildMessage(Message message) {
    return singletonInjector(Message.class, message).getInstance(JdaGuildMessage.class);
  }
  
  /**
   * Instantiates a new guild member. A member can also be a normal user.
   *
   * @param member A JDA member.
   * @return A new API instance of the member.
   */
  public static JdaMember injectMember(Member member) {
    AbstractModule m1 = singletonModule(User.class, member.getUser());
    AbstractModule m2 = singletonModule(Member.class, member);
  
    return Guice.createInjector(m1, m2).getInstance(JdaMember.class);
  }
  
  /**
   * Instantiates a new private channel. A private channel can also be a normal message channel.
   *
   * @param channel A JDA private channel.
   * @return A new API instance of the channel.
   */
  public static JdaPrivateChannel injectPrivateChannel(PrivateChannel channel) {
    AbstractModule m1 = singletonModule(PrivateChannel.class, channel);
    AbstractModule m2 = singletonModule(MessageChannel.class, channel);
  
    return Guice.createInjector(m1, m2).getInstance(JdaPrivateChannel.class);
  }
  
  public static JdaPrivateMessage injectPrivateMessage(Message message) {
    return singletonInjector(Message.class, message).getInstance(JdaPrivateMessage.class);
  }
  
  public static JdaRole injectRole(Role role) {
    return singletonInjector(Role.class, role).getInstance(JdaRole.class);
  }
  
  /**
   * Instantiates a new self member. A self member can also be a self user.
   *
   * @param member A JDA self member.
   * @return A new API instance of the self member.
   */
  public static JdaSelfMember injectSelfMember(Member member) {
    AbstractModule m1 = singletonModule(User.class, member.getUser());
    AbstractModule m2 = singletonModule(Member.class, member);
    
    return Guice.createInjector(m1, m2).getInstance(JdaSelfMember.class);
  }
  
  public static JdaSelfUser injectSelfUser(SelfUser user) {
    return singletonInjector(SelfUser.class, user).getInstance(JdaSelfUser.class);
  }
  
  /**
   * Instantiates a new shard. New commands are executed using the executor service shared among
   * all entities of this shard.
   *
   * @param jda A JDA instance.
   * @return A new API instance of the shard.
   */
  public static JdaShard injectShard(JDA jda) {
    AbstractModule m1 = singletonModule(JDA.class, jda);
    AbstractModule m2 = singletonModule(ExecutorService.class, queue.getUnchecked(jda));
    
    return Guice.createInjector(m1, m2).getInstance(JdaShard.class);
  }
  
  /**
   * Instantiates a new text channel. A text channel can also be a normal message channel.
   *
   * @param channel A JDA text channel.
   * @return A new API instance of the channel.
   */
  public static JdaTextChannel injectTextChannel(TextChannel channel) {
    AbstractModule m1 = singletonModule(TextChannel.class, channel);
    AbstractModule m2 = singletonModule(MessageChannel.class, channel);
    
    return Guice.createInjector(m1, m2).getInstance(JdaTextChannel.class);
  }
  
  public static JdaUser injectUser(User user) {
    return singletonInjector(User.class, user).getInstance(JdaUser.class);
  }
  
  /**
   * Instantiates a new web hook. New messages are sent using the executor service shared among
   * the given shard.
   *
   * @param webHook A JDA web hook.
   * @return A new API instance of the web hook.
   */
  public static JdaWebHook injectWebHook(Webhook webHook) {
    JDAWebhookClient webHookClient = WebhookClientBuilder.fromJDA(webHook)
          .setExecutorService(queue.getUnchecked(webHook.getJDA()))
          .buildJDA();
    
    AbstractModule m1 = singletonModule(JDAWebhookClient.class, webHookClient);
    AbstractModule m2 = singletonModule(Webhook.class, webHook);
    
    return Guice.createInjector(m1, m2).getInstance(JdaWebHook.class);
  }
}
