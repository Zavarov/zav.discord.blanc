package zav.discord.blanc.runtime.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.cache.AutoResponseCache;
import zav.discord.blanc.api.cache.PatternCache;
import zav.discord.blanc.command.CommandManager;
import zav.discord.blanc.databind.Credentials;
import zav.discord.blanc.reddit.SubredditObservable;

/**
 * Abstract base class for all unit tests that need to interact with the Discord API.
 * This class provides mocks for all basic JDA instances.
 */
public class AbstractTest {
  public @Mock Client client;
  public @Mock Guild guild;
  public @Mock Member member;
  public @Mock Member selfMember;
  public @Mock User user;
  public @Mock TextChannel channel;
  public @Mock JDA jda;
  public @Mock Presence presence;
  public @Mock RestAction<List<Webhook>> retrieveWebhooks;
  public @Mock WebhookAction createWebhook;
  public @Mock Webhook webhook;
  public @Mock AuditableRestAction<Void> delete;
  public @Mock SubredditObservable subredditObservable;
  public @Mock EntityManagerFactory entityManagerFactory;
  public @Mock EntityManager entityManager;
  public @Mock EntityTransaction entityTransaction;
  public @Mock ScheduledExecutorService queue;
  public @Mock PatternCache patternCache;
  public @Mock AutoResponseCache responseCache;
  
  public @Captor ArgumentCaptor<String> response;
  public @Mock SlashCommandEvent event;
  public @Mock CommandManager manager;
  public @Mock ReplyAction reply;
  public @Mock Credentials credentials;
  
  /**
   * Initializes the getter and setter methods of the individual mocks.
   */
  @BeforeEach
  public void initMocks() {
    lenient().when(client.getShards()).thenReturn(List.of(jda));
    lenient().when(client.getSubredditObservable()).thenReturn(subredditObservable);
    lenient().when(client.getEntityManagerFactory()).thenReturn(entityManagerFactory);
    lenient().when(client.getEventQueue()).thenReturn(queue);
    lenient().when(client.getPatternCache()).thenReturn(patternCache);
    lenient().when(client.getCredentials()).thenReturn(credentials);
    lenient().when(client.getAutoResponseCache()).thenReturn(responseCache);
    
    lenient().when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
    lenient().when(entityManager.getTransaction()).thenReturn(entityTransaction);
    
    lenient().when(jda.getGuilds()).thenReturn(List.of(guild));
    lenient().when(jda.getPresence()).thenReturn(presence);
    lenient().when(guild.getJDA()).thenReturn(jda);
    lenient().when(guild.getSelfMember()).thenReturn(selfMember);
    lenient().when(guild.getTextChannels()).thenReturn(List.of(channel));
    lenient().when(guild.getTextChannelById(anyLong())).thenReturn(channel);
    lenient().when(channel.getGuild()).thenReturn(guild);
    lenient().when(channel.retrieveWebhooks()).thenReturn(retrieveWebhooks);
    lenient().when(channel.createWebhook(anyString())).thenReturn(createWebhook);
    lenient().when(createWebhook.complete()).thenReturn(webhook);
    lenient().when(retrieveWebhooks.complete()).thenReturn(List.of(webhook));
    lenient().when(manager.getClient()).thenReturn(client);

    lenient().when(event.getMember()).thenReturn(member);
    lenient().when(event.getGuild()).thenReturn(guild);
    lenient().when(event.getUser()).thenReturn(user);
    lenient().when(event.getTextChannel()).thenReturn(channel);
    lenient().when(event.reply(anyString())).thenReturn(reply);
    lenient().when(event.replyFormat(anyString(), nullable(String.class))).thenReturn(reply);
    lenient().when(event.reply(response.capture())).thenReturn(reply);
    lenient().when(event.replyEmbeds(any(MessageEmbed.class))).thenReturn(reply);
    lenient().when(reply.setEphemeral(anyBoolean())).thenReturn(reply);
  }
}
