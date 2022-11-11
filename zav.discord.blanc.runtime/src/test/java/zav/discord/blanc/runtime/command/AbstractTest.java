package zav.discord.blanc.runtime.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.Presence;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.WebhookAction;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.api.cache.AutoResponseCache;
import zav.discord.blanc.api.cache.PatternCache;
import zav.discord.blanc.command.CommandManager;
import zav.discord.blanc.databind.AutoResponseEntity;
import zav.discord.blanc.databind.Credentials;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.databind.WebhookEntity;
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
  public @Mock SelfUser  selfUser;
  public @Mock TextChannel channel;
  public @Mock JDA jda;
  public @Mock Shard shard;
  public @Mock Presence presence;
  public @Mock RestAction<List<Webhook>> retrieveWebhooks;
  public @Mock WebhookAction createWebhook;
  public @Mock Webhook webhook;
  public @Mock AuditableRestAction<Void> delete;
  public @Mock SubredditObservable subredditObservable;
  public @Mock ScheduledExecutorService queue;
  public @Mock PatternCache patternCache;
  public @Mock AutoResponseCache responseCache;
  
  public @Captor ArgumentCaptor<String> response;
  public @Mock SlashCommandEvent event;
  public @Mock CommandManager manager;
  public @Mock ReplyAction reply;
  public @Mock Credentials credentials;
  
  public GuildEntity guildEntity;
  public TextChannelEntity channelEntity;
  public WebhookEntity webhookEntity;
  public UserEntity userEntity;
  public AutoResponseEntity responseEntity;

  private MockedStatic<GuildEntity> mocked1;
  private MockedStatic<TextChannelEntity> mocked2;
  private MockedStatic<WebhookEntity> mocked3;
  private MockedStatic<UserEntity> mocked4;

  /**
   * Initializes the getter and setter methods of the individual mocks.
   */
  @BeforeEach
  public void initMocks() {
    lenient().when(client.getShards()).thenReturn(List.of(shard));
    lenient().when(client.get(SubredditObservable.class)).thenReturn(subredditObservable);
    lenient().when(client.get(Credentials.class)).thenReturn(credentials);
    lenient().when(shard.get(ScheduledExecutorService.class)).thenReturn(queue);
    lenient().when(shard.get(PatternCache.class)).thenReturn(patternCache);
    lenient().when(shard.get(AutoResponseCache.class)).thenReturn(responseCache);
    lenient().when(shard.getJda()).thenReturn(jda);
    lenient().when(shard.getClient()).thenReturn(client);
    
    lenient().when(jda.getSelfUser()).thenReturn(selfUser);
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
    lenient().when(webhook.getOwner()).thenReturn(selfMember);
    lenient().when(manager.getShard()).thenReturn(shard);

    lenient().when(event.getJDA()).thenReturn(jda);
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

  /**
   * Initializes all Discord entities.
   */
  @BeforeEach
  public void initEntities() {
    guildEntity = spy(new GuildEntity());
    channelEntity = spy(new TextChannelEntity());
    webhookEntity = spy(new WebhookEntity());
    userEntity = spy(new UserEntity());
    responseEntity = spy(new AutoResponseEntity());
    
    lenient().doNothing().when(guildEntity).merge();
    lenient().doNothing().when(channelEntity).merge();
    lenient().doNothing().when(webhookEntity).merge();
    lenient().doNothing().when(userEntity).merge();
    lenient().doNothing().when(responseEntity).merge();

    guildEntity.add(channelEntity);
    guildEntity.add(webhookEntity);
    guildEntity.add(responseEntity);
    channelEntity.add(webhookEntity);

    mocked1 = mockStatic(GuildEntity.class);
    mocked1.when(() -> GuildEntity.find(guild)).thenReturn(guildEntity);
    mocked2 = mockStatic(TextChannelEntity.class);
    mocked2.when(() -> TextChannelEntity.find(channel)).thenReturn(channelEntity);
    mocked3 = mockStatic(WebhookEntity.class);
    mocked3.when(() -> WebhookEntity.find(webhook)).thenReturn(webhookEntity);
    mocked4 = mockStatic(UserEntity.class);
    mocked4.when(() -> UserEntity.find(user)).thenReturn(userEntity);
  }

  /**
   * Close all static mocks.
   */
  @AfterEach
  public void tearDownEntities() {
    mocked1.close();
    mocked2.close();
    mocked3.close();
    mocked4.close();
  }
}
