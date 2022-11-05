package zav.discord.blanc.databind;

import zav.discord.blanc.databind.internal.PersistenceUtil;

public interface PersistedEntity {

  long getId();

  default void merge() {
    PersistenceUtil.merge(this);
  }

  default boolean isPersisted() {
    return PersistenceUtil.find(getClass(), getId()) != null;
  }
}
