package zav.discord.blanc.runtime.job;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.WebhookEntity;

/**
 * This test case checks whether invalid text channels and webhooks are deleted from the database.
 */
@ExtendWith(MockitoExtension.class)
public class CleanupJobTest {
  @Mock EntityManagerFactory factory;
  @Mock EntityTransaction transaction;
  @Mock EntityManager manager;
  @Mock TextChannel textChannel;
  @Mock Client client;
  @Mock Guild guild;
  @Mock JDA jda;
  
  TextChannelEntity channelEntity;
  WebhookEntity webhookEntity;
  GuildEntity guildEntity;
  CleanupJob job;
  
  /**
   * Initializes the cleanup job with a mocked database. The database contains both a webhook and a
   * text-channel.
   */
  @BeforeEach
  public void setUp() {
    webhookEntity = new WebhookEntity();
    channelEntity = new TextChannelEntity();
    channelEntity.add(webhookEntity);
    guildEntity = new GuildEntity();
    guildEntity.add(channelEntity);
    guildEntity.add(webhookEntity);
    
    when(factory.createEntityManager()).thenReturn(manager);
    when(manager.find(eq(GuildEntity.class), anyLong())).thenReturn(guildEntity);
    when(manager.getTransaction()).thenReturn(transaction);
    when(client.getEntityManagerFactory()).thenReturn(factory);
    when(client.getShards()).thenReturn(List.of(jda));
    when(jda.getGuilds()).thenReturn(List.of(guild));
    when(guild.getTextChannelById(anyLong())).thenReturn(textChannel);

    job = new CleanupJob(client);
  }
  
  @Test
  public void testRemoveTextChannel() {
    job.run();
    
    assertEquals(guildEntity.getTextChannels(), Collections.emptyList());
  }
  
  @Test
  public void testRemoveWebhookl() {
    job.run();

    assertEquals(guildEntity.getWebhooks(), Collections.emptyList());
  }
}
