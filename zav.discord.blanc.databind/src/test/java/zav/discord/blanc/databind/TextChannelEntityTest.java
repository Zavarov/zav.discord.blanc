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
public class TextChannelEntityTest {
  
  EntityManagerFactory factory;
  @Mock Guild guild;
  @Mock TextChannel channel;
  @Mock Webhook webhook;
  
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
  
  private void removeTextChannel() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      TextChannelEntity entity = entityManager.find(TextChannelEntity.class, channel.getIdLong());
      
      entityManager.getTransaction().begin();
      entityManager.remove(entity);
      entityManager.getTransaction().commit();
    }
  }
  
  /**
   * Use Case: The webhook is still referenced by the guild, so we have too keep it.
   */
  @Test
  public void testRemoveChannelKeepsWebhook() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNotNull(entityManager.find(WebhookEntity.class, webhook.getIdLong()));
      assertNotNull(entityManager.find(TextChannelEntity.class, channel.getIdLong()));
    }
    
    removeTextChannel();
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNotNull(entityManager.find(WebhookEntity.class, webhook.getIdLong()));
      assertNull(entityManager.find(TextChannelEntity.class, channel.getIdLong()));
    }
  }
  
  /**
   * Use Case: The text channel is contained by the guild. Removing it doesn't remove the guild.
   */
  @Test
  public void testRemoveChannelKeepsGuild() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNotNull(entityManager.find(GuildEntity.class, guild.getIdLong()));
      assertNotNull(entityManager.find(TextChannelEntity.class, channel.getIdLong()));
    }
    
    removeTextChannel();
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNotNull(entityManager.find(GuildEntity.class, guild.getIdLong()));
      assertNull(entityManager.find(TextChannelEntity.class, channel.getIdLong()));
    }
  }
}
