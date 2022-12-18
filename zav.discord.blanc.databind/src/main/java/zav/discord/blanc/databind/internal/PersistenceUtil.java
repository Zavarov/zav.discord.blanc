package zav.discord.blanc.databind.internal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.PersistenceException;
import java.util.List;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.ISnowflake;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Webhook;
import zav.discord.blanc.databind.GuildEntity;
import zav.discord.blanc.databind.Rank;
import zav.discord.blanc.databind.TextChannelEntity;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.databind.WebhookEntity;

/**
 * Utility class managing the database access.
 */
public abstract class PersistenceUtil {
  /**
   * The name of the JPA persistence unit containing all managed entities.
   */
  public static final String PERSISTENCE_UNIT_NAME = "discord-entities";
  
  private static EntityManagerFactory factory;
  
  private PersistenceUtil() {
  }
  
  /* package */ static void setEntityManagerFactory(EntityManagerFactory factory) {
    closeEntityManagerFactory();
    
    PersistenceUtil.factory = factory;
  }
  
  /* package */ static void openEntityManagerFactory() {
    closeEntityManagerFactory();
    
    try {
      factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    } catch (PersistenceException ignored) {
      // May be null in the OSGi world.
    }
  }
  
  /* package */ static void closeEntityManagerFactory() {
    if (factory != null && factory.isOpen()) {
      factory.close();
    }
    
    factory = null;
  }
  
  static {
    openEntityManagerFactory();
  }
  
  /**
   * Retrieves the entity corresponding to the provided guild from the database.
   * If no such entry exists, a new entity is created. The name of the entity is
   * updated, as it might have changed since it was persisted.
   *
   * @param guild A Discord guild.
   * @return The entity corresponding to the guild.
   */
  public static GuildEntity find(Guild guild) {
    GuildEntity entity = find(GuildEntity.class, guild.getIdLong());
    
    if (entity == null) {
      entity = new GuildEntity();
      entity.setId(guild.getIdLong());
    }
    
    // Guild name may have changed since the last time the entity was persisted
    entity.setName(guild.getName());
    
    return entity;
  }
  
  /**
   * Retrieves the entity corresponding to the provided channel from the database.
   * If no such entry exists, a new entity is created. The name of the entity is
   * updated, as it might have changed since it was persisted.
   *
   * @param channel A Discord text channel.
   * @return The entity corresponding to the channel.
   */
  public static TextChannelEntity find(TextChannel channel) {
    TextChannelEntity entity = find(TextChannelEntity.class, channel.getIdLong());
    
    if (entity == null) {
      entity = new TextChannelEntity();
      entity.setId(channel.getIdLong());
    }
    
    // Text-channel name may have changed since the last time the entity was
    // persisted
    entity.setName(channel.getName());
    
    return entity;
  }
  
  /**
   * Retrieves the entity corresponding to the provided user from the database. If
   * no such entry exists, a new entity is created. The name and discriminator of
   * the entity is updated, as it might have changed since it was persisted.
   *
   * @param user A Discord user.
   * @return The entity corresponding to the user.
   */
  public static UserEntity find(User user) {
    UserEntity entity = find(UserEntity.class, user.getIdLong());
    
    if (entity == null) {
      entity = new UserEntity();
      entity.setId(user.getIdLong());
      entity.setRanks(List.of(Rank.USER));
    }
    
    // User name and discriminator may have changed since the last time the entity
    // was persisted
    entity.setName(user.getName());
    entity.setDiscriminator(user.getDiscriminator());
    
    return entity;
  }
  
  /**
   * Retrieves the entity corresponding to the provided webhook from the database.
   * If no such entry exists, a new entity is created. The name of the entity is
   * updated, as it might have changed since it was persisted.
   *
   * @param webhook A Discord webhook.
   * @return The entity corresponding to the webhook.
   */
  public static WebhookEntity find(Webhook webhook) {
    WebhookEntity entity = find(WebhookEntity.class, webhook.getIdLong());
    
    if (entity == null) {
      entity = new WebhookEntity();
      entity.setId(webhook.getIdLong());
    }
    
    // Webhook name may have changed since the last time the entity was persisted
    entity.setName(webhook.getName());
    
    return entity;
  }
  
  /**
   * Retrieves the entity from the database.
   *
   * @param <T>        The entity type.
   * @param clazz      The entity class.
   * @param primaryKey The unique id of the requested entity.
   * @return The persisted object or {@code null} if no such entry exists.
   */
  public static <T> T find(Class<T> clazz, Object primaryKey) {
    try (EntityManager entityManager = factory.createEntityManager()) {
      return entityManager.find(clazz, primaryKey);
    }
  }
  
  /**
   * Removes the Discord object from the database. The corresponding entity is
   * determined using the unique id.
   *
   * @param <T>       The entity type.
   * @param clazz     The entity class.
   * @param snowflake A Discord object.
   */
  public static <T> void remove(Class<T> clazz, ISnowflake snowflake) {
    remove(GuildEntity.class, snowflake.getIdLong());
  }
  
  /**
   * Removes the given entity from the database.
   * 
   * @param <T>        The entity type.
   * @param clazz      The entity class.
   * @param primaryKey The unique id of the removed entity.
   */
  public static <T> void remove(Class<T> clazz, Object primaryKey) {
    try (EntityManager entityManager = factory.createEntityManager()) {
      T entity = entityManager.find(clazz, primaryKey);
      
      if (entity != null) {
        entityManager.getTransaction().begin();
        entityManager.remove(entity);
        entityManager.getTransaction().commit();
      }
    }
  }
  
  /**
   * Writes the entity to the database.
   *
   * @param <T>    The entity type.
   * @param entity The entity object.
   */
  public static <T> void merge(T entity) {
    try (EntityManager entityManager = factory.createEntityManager()) {
      entityManager.getTransaction().begin();
      entityManager.merge(entity);
      entityManager.getTransaction().commit();
    }
  }
}
