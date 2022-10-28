package zav.discord.blanc.databind;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.Webhook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WebhookEntityTest {
  
  EntityManagerFactory factory;
  @Mock Guild guild;
  @Mock TextChannel channel;
  @Mock Webhook webhook;
  
  static {
    System.setProperty("org.jboss.logging.provider", "slf4j");
  }
  
  @BeforeEach
  public void setUp() {
    factory = Persistence.createEntityManagerFactory("discord-entities");
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity guildEntity = GuildEntity.getOrCreate(entityManager, guild);
      TextChannelEntity channelEntity = TextChannelEntity.getOrCreate(entityManager, channel);
      WebhookEntity webhookEntity = WebhookEntity.getOrCreate(entityManager, webhook);
      
      guildEntity.add(webhookEntity);
      guildEntity.add(channelEntity);
      
      entityManager.getTransaction().begin();
      entityManager.merge(guildEntity);
      entityManager.getTransaction().commit();
    }
  }
  
  @AfterEach
  public void tearDown() {
    factory.close();
  }
  
  private void removeWebhook() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      WebhookEntity entity = entityManager.find(WebhookEntity.class, webhook.getIdLong());
      
      entityManager.getTransaction().begin();
      entityManager.remove(entity);
      entityManager.getTransaction().commit();
    }
  }
  
  /**
   * Use Case: The webhook is contained by the text channel. Removing it doesn't remove the channel.
   */
  @Test
  public void testRemoveWebhookKeepsChannel() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNotNull(entityManager.find(TextChannelEntity.class, channel.getIdLong()));
      assertNotNull(entityManager.find(WebhookEntity.class, webhook.getIdLong()));
    }
    
    removeWebhook();
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNotNull(entityManager.find(TextChannelEntity.class, channel.getIdLong()));
      assertNull(entityManager.find(WebhookEntity.class, webhook.getIdLong()));
    }
  }
  
  /**
   * Use Case: The webhook is contained by the guild. Removing it doesn't remove the guild.
   */
  @Test
  public void testRemoveWebhookKeepsGuild() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNotNull(entityManager.find(GuildEntity.class, guild.getIdLong()));
      assertNotNull(entityManager.find(WebhookEntity.class, webhook.getIdLong()));
    }
    
    removeWebhook();
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNotNull(entityManager.find(GuildEntity.class, guild.getIdLong()));
      assertNull(entityManager.find(WebhookEntity.class, webhook.getIdLong()));
    }
  }
}
