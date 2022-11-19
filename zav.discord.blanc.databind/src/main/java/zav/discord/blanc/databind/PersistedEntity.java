package zav.discord.blanc.databind;

import zav.discord.blanc.databind.internal.PersistenceUtil;

/**
 * Base interface for all entities. It provides basic methods for interacting with the database.
 */
public interface PersistedEntity {

  /**
   * Returns the unique id of this entity.
   *
   * @return As described.
   */
  long getId();

  /**
   * Writes this entity to the database.
   */
  default void merge() {
    PersistenceUtil.merge(this);
  }

  /**
   * Checks whether the entity is persisted. 
   *
   * @return {@code true} if the entity is persisted, otherwise {@code false}.
   */
  default boolean isPersisted() {
    return PersistenceUtil.find(getClass(), getId()) != null;
  }
}
