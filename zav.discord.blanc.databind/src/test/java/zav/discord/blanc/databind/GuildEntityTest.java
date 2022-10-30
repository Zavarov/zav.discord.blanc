package zav.discord.blanc.databind;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.function.Consumer;
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
  private static final long AUTORESPONSE_ID = 0L;
  
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
  
  /**
   * Use Case: Removing a guild should also remove its text channels.
   */
  @Test
  public void testRemoveGuildRemovesTextChannel() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNotNull(getGuildEntity(entityManager));
      assertNotNull(getChannelEntity(entityManager));
    }
    
    removeGuild();
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNull(getGuildEntity(entityManager));
      assertNull(getChannelEntity(entityManager));
    }
  }
  
  /**
   * Use Case: Removing a guild should also remove its webhooks.
   */
  @Test
  public void testRemoveGuildRemovesWebhook() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNotNull(getGuildEntity(entityManager));
      assertNotNull(getWebhookEntity(entityManager));
    }
    
    removeGuild();
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNull(getGuildEntity(entityManager));
      assertNull(getWebhookEntity(entityManager));
    }
  }
  
  /**
   * Use Case: Removing a guild should also remove its auto responses.
   */
  @Test
  public void testRemoveGuildRemovesAutoResponse() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNotNull(getGuildEntity(entityManager));
      assertNotNull(getResponseEntity(entityManager));
    }
    
    removeGuild();
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      assertNull(getGuildEntity(entityManager));
      assertNull(getResponseEntity(entityManager));
    }
  }
  
  /**
   * Use Case: Updating a guild should also update referenced text channels.
   */
  @Test
  public void testMergeGuildUpdatesTextChannel() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity guildEntity = getGuildEntity(entityManager);
      
      modifyChannel(entityManager, entity -> entity.setName("foo"));
      
      entityManager.getTransaction().begin();
      entityManager.merge(guildEntity);
      entityManager.getTransaction().commit();
    }
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      modifyChannel(entityManager, entity -> assertEquals(entity.getName(), "foo"));
    }
  }
  
  /**
   * Use Case: Updating a guild should also update referenced webhooks.
   */
  @Test
  public void testMergeGuildUpdatesWebhook() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity guildEntity = getGuildEntity(entityManager);
      
      modifyWebhook(entityManager, entity -> entity.setName("foo"));
      
      entityManager.getTransaction().begin();
      entityManager.merge(guildEntity);
      entityManager.getTransaction().commit();
    }
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      modifyWebhook(entityManager, entity -> assertEquals(entity.getName(), "foo"));
    }
  }
  
  /**
   * Use Case: Updating a guild should also update referenced responses.
   */
  @Test
  public void testMergeGuildUpdatesAutoResponse() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity guildEntity = getGuildEntity(entityManager);
      
      modifyResponse(entityManager, entity -> entity.setPattern("foo"));
      
      entityManager.getTransaction().begin();
      entityManager.merge(guildEntity);
      entityManager.getTransaction().commit();
    }
    
    try (EntityManager entityManager = factory.createEntityManager()) {
      modifyResponse(entityManager, entity -> assertEquals(entity.getPattern(), "foo"));
    }
  }
  
  // Get Entities
  
  private GuildEntity getGuildEntity(EntityManager entityManager) {
    return entityManager.find(GuildEntity.class, guild.getIdLong());
  }
  
  private WebhookEntity getWebhookEntity(EntityManager entityManager) {
    return entityManager.find(WebhookEntity.class, webhook.getIdLong());
  }
  
  private TextChannelEntity getChannelEntity(EntityManager entityManager) {
    return entityManager.find(TextChannelEntity.class, channel.getIdLong());
  }
  
  private AutoResponseEntity getResponseEntity(EntityManager entityManager) {
    return entityManager.find(AutoResponseEntity.class, AUTORESPONSE_ID);
  }
  
  // Modify Entities
  
  private void modifyWebhook(EntityManager entityManager, Consumer<WebhookEntity> consumer) {
    consumer.accept(getWebhookEntity(entityManager));
  }
  
  private void modifyChannel(EntityManager entityManager, Consumer<TextChannelEntity> consumer) {
    consumer.accept(getChannelEntity(entityManager));
  }
  
  private void modifyResponse(EntityManager entityManager, Consumer<AutoResponseEntity> consumer) {
    consumer.accept(getResponseEntity(entityManager));
  }
  
  // Remove Guild
  
  private void removeGuild() {
    try (EntityManager entityManager = factory.createEntityManager()) {
      GuildEntity entity = getGuildEntity(entityManager);
      
      entityManager.getTransaction().begin();
      entityManager.remove(entity);
      entityManager.getTransaction().commit();
    }
  }
}
