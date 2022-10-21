package zav.discord.blanc.runtime.command;

import static org.mockito.Mockito.lenient;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

/**
 * The base class for all tests that have to communicate with the database.<br>
 * It mocks the entity manager which is normally used to perform all interactions.
 *
 * @param <T> The persisted entity type.
 */
public class AbstractDatabaseTest<T> extends AbstractTest {
  public @Captor ArgumentCaptor<T> captor;
  public T entity;
  
  /**
   * Mocks the database access. Searching the database for the given
   * entity type always returns the provided instance.
   *
   * @param entity An instance of the entity that is persisted.
   */
  public void setUp(T entity) {
    lenient().when(entityManager.merge(captor.capture())).thenReturn(null);
    this.entity = entity;
  }
}
