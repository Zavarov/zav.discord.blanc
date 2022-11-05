package zav.discord.blanc.runtime.internal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.function.BiFunction;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import zav.discord.blanc.databind.GuildEntity;

public class PersistenceUtils {
  private PersistenceUtils() {}

  public static void handle(EntityManagerFactory factory, SlashCommandEvent event, BiFunction<EntityManager, GuildEntity, String> consumer) {
    try (EntityManager entityManager = factory.createEntityManager()) {
      final GuildEntity entity = GuildEntity.getOrCreate(entityManager, event.getGuild());
      final String response = consumer.apply(entityManager, entity);

      // Write changes to the database
      entityManager.getTransaction().begin();
      entityManager.merge(entity);
      entityManager.getTransaction().commit();

      event.reply(MarkdownSanitizer.escape(response)).complete();
    }
  }
}
