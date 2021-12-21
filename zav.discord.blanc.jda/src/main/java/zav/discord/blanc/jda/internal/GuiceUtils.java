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
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
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
import net.dv8tion.jda.api.managers.Presence;
import zav.discord.blanc.jda.api.*;

/**
 * Utility class for instantiating all of the API implementations using Guice.
 */
public final class GuiceUtils {
  
  private static <T> AbstractModule singletonModule(Class<T> source, T value) {
    return new AbstractModule() {
      @Override
      protected void configure() {
        bind(source).toInstance(value);
      }
    };
  }
  
  private static Injector injector;
  
  public static void setInjector(Injector injector) {
    GuiceUtils.injector = injector;
  }
  
  /**
   * Instantiates a new guild.
   *
   * @param guild A JDA message.
   * @return A new API instance of the guild.
   */
  public static JdaGuild injectGuild(Guild guild) {
    AbstractModule m1 = singletonModule(Guild.class, guild);
    
    return injector.createChildInjector(m1).getInstance(JdaGuild.class);
  }
  
  /**
   * Instantiates a new guild message.
   *
   * @param message A JDA message.
   * @return A new API instance of the guild message.
   */
  public static JdaGuildMessage injectGuildMessage(Message message) {
    AbstractModule m1 = singletonModule(Message.class, message);
    
    return injector.createChildInjector(m1).getInstance(JdaGuildMessage.class);
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
  
    return injector.createChildInjector(m1, m2).getInstance(JdaMember.class);
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
  
    return injector.createChildInjector(m1, m2).getInstance(JdaPrivateChannel.class);
  }
  
  /**
   * Instantiates a new private message.
   *
   * @param message A JDA message.
   * @return A new API instance of the private message.
   */
  public static JdaPrivateMessage injectPrivateMessage(Message message) {
    AbstractModule m1 = singletonModule(Message.class, message);
    
    return injector.createChildInjector(m1).getInstance(JdaPrivateMessage.class);
  }
  
  /**
   * Instantiates a new role.
   *
   * @param role A JDA role.
   * @return A new API instance of the role.
   */
  public static JdaRole injectRole(Role role) {
    AbstractModule m1 = singletonModule(Role.class, role);
    
    return injector.createChildInjector(m1).getInstance(JdaRole.class);
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
    
    return injector.createChildInjector(m1, m2).getInstance(JdaSelfMember.class);
  }
  
  /**
   * Instantiates a new self user.
   *
   * @param user A JDA self user.
   * @return A new API instance of the self user.
   */
  public static JdaSelfUser injectSelfUser(SelfUser user) {
    AbstractModule m1 = singletonModule(SelfUser.class, user);
    
    return injector.createChildInjector(m1).getInstance(JdaSelfUser.class);
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
    
    return injector.createChildInjector(m1).getInstance(JdaShard.class);
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
    
    return injector.createChildInjector(m1, m2).getInstance(JdaTextChannel.class);
  }
  
  /**
   * Instantiates a new user.
   *
   * @param user A JDA user.
   * @return A new API instance of the user.
   */
  public static JdaUser injectUser(User user) {
    AbstractModule m1 = singletonModule(User.class, user);
    
    return injector.createChildInjector(m1).getInstance(JdaUser.class);
  }
  
  /**
   * Instantiates a new web hook. New messages are sent using the executor service shared among
   * the given shard.
   *
   * @param webHook A JDA web hook.
   * @return A new API instance of the web hook.
   */
  public static JdaWebHook injectWebHook(Webhook webHook) {
    ScheduledExecutorService queue = injector.getInstance(ScheduledExecutorService.class);
    
    JDAWebhookClient webHookClient = WebhookClientBuilder.fromJDA(webHook)
          .setExecutorService(queue)
          .buildJDA();
    
    AbstractModule m1 = singletonModule(JDAWebhookClient.class, webHookClient);
    AbstractModule m2 = singletonModule(Webhook.class, webHook);
    
    return injector.createChildInjector(m1, m2).getInstance(JdaWebHook.class);
  }
  
  /**
   * Instantiates a new presence of the application in the current shard.
   *
   * @param presence A JDA presence.
   * @return A new API instance of the presence.
   */
  public static JdaPresence injectPresence(Presence presence) {
    AbstractModule m1 = singletonModule(Presence.class, presence);
    
    return injector.createChildInjector(m1).getInstance(JdaPresence.class);
  }
}
