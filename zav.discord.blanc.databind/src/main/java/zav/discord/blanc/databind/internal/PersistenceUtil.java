package zav.discord.blanc.databind.internal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
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

public abstract class PersistenceUtil {
  private static final EntityManagerFactory FACTORY = Persistence.createEntityManagerFactory("discord-entities");
  private PersistenceUtil() {}

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

  public static TextChannelEntity find(TextChannel channel) {
    TextChannelEntity entity = find(TextChannelEntity.class, channel.getIdLong());

    if (entity == null) {
      entity = new TextChannelEntity();
      entity.setId(channel.getIdLong());
    }

    // Text-channel name may have changed since the last time the entity was persisted
    entity.setName(channel.getName());

    return entity;
  }

  public static UserEntity find(User user) {
    UserEntity entity = find(UserEntity.class, user.getIdLong());
    
    if (entity == null) {
      entity = new UserEntity();
      entity.setId(user.getIdLong());
      entity.setRanks(List.of(Rank.USER));
    }
    
    // User name and discriminator may have changed since the last time the entity was persisted
    entity.setName(user.getName());
    entity.setDiscriminator(user.getDiscriminator());
    
    return entity;
  }

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

  public static <T, U> T find(Class<T> clazz, Object primaryKey) {
    try (EntityManager entityManager = FACTORY.createEntityManager()) {
      return entityManager.find(clazz, primaryKey);
    }
  }

  public static void remove(ISnowflake snowflake) {
    remove(GuildEntity.class, snowflake.getIdLong());
  }

  public static <T> void remove(Class<T> clazz, Object primaryKey) {
    try (EntityManager entityManager = FACTORY.createEntityManager()) {
      T entity = entityManager.find(clazz, primaryKey);
      
      if (entity != null) {
        entityManager.getTransaction().begin();
        entityManager.remove(entity);
        entityManager.getTransaction().commit();
      }
    }
  }

  public static <T> void merge(T entity) {
    try (EntityManager entityManager = FACTORY.createEntityManager()) {
      entityManager.getTransaction().begin();
      entityManager.merge(entity);
      entityManager.getTransaction().commit();
    }
  }
}
