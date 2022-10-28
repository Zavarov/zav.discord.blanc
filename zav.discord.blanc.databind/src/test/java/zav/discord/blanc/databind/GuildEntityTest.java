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
public class GuildEntityTest {
  // Arbitrary but fixed. Because of GenerationType.IDENTITY, the first element gets the id 1
  private static final long AUTORESPONSE_ID = 1L;
  
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
      AutoResponseEntity responseEntity = AutoResponseEntity.create("foo", "bar");
      
      guildEntity.add(webhookEntity);
      guildEntity.add(responseEntity);
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
  
  private void removeGuild() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity entity = entityManager.find(GuildEntity.class, guild.getIdLong());
      
      entityManager.getTransaction().begin();
      entityManager.remove(entity);
      entityManager.getTransaction().commit();
    }
  }
  
  @Test
  public void testRemoveGuildRemovesTextChannel() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNotNull(entityManager.find(GuildEntity.class, guild.getIdLong()));
      assertNotNull(entityManager.find(TextChannelEntity.class, channel.getIdLong()));
    }
    
    removeGuild();
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNull(entityManager.find(GuildEntity.class, guild.getIdLong()));
      assertNull(entityManager.find(TextChannelEntity.class, channel.getIdLong()));
    }
  }
  
  @Test
  public void testRemoveGuildRemovesWebhook() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNotNull(entityManager.find(GuildEntity.class, guild.getIdLong()));
      assertNotNull(entityManager.find(WebhookEntity.class, webhook.getIdLong()));
    }
    
    removeGuild();
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNull(entityManager.find(GuildEntity.class, guild.getIdLong()));
      assertNull(entityManager.find(WebhookEntity.class, webhook.getIdLong()));
    }
  }
  
  @Test
  public void testRemoveGuildRemovesAutoResponse() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNotNull(entityManager.find(GuildEntity.class, guild.getIdLong()));
      assertNotNull(entityManager.find(AutoResponseEntity.class, AUTORESPONSE_ID));
    }
    
    removeGuild();
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNull(entityManager.find(GuildEntity.class, guild.getIdLong()));
      assertNull(entityManager.find(AutoResponseEntity.class, AUTORESPONSE_ID));
    }
  }
}
