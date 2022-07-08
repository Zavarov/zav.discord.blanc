package zav.discord.blanc.runtime.command;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import zav.discord.blanc.api.Client;

/**
 * The base class for all tests that have to communicate with the database.<br>
 * It mocks the entity manager which is normally used to perform all interactions.
 *
 * @param <T> The persisted entity type.
 */
public class AbstractDatabaseTest<T> {
  protected @Captor ArgumentCaptor<T> captor;
  protected @Mock EntityManagerFactory factory;
  protected @Mock EntityManager entityManager;
  protected @Mock EntityTransaction transaction;
  protected @Mock Client client;
  protected T entity;
  
  /**
   * Mocks the database access. Searching the database for the given
   * entity type always returns the provided instance.
   *
   * @param entity An instance of the entity that is persisted.
   */
  public void setUp(T entity) {
    when(client.getEntityManagerFactory()).thenReturn(factory);
    when(factory.createEntityManager()).thenReturn(entityManager);
    lenient().when(entityManager.getTransaction()).thenReturn(transaction);
    lenient().when(entityManager.merge(captor.capture())).thenReturn(null);
    this.entity = entity;
  }
}
