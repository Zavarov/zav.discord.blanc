package zav.discord.blanc.job;

/**
 * This interface should be implement by any class that should be executed by an asynchronous thread
 * within the application.<br>
 * It is heavily inspired by the {@link Runnable} interface, with the only exception being that the
 * {@code run} method is allowed to throw an exception.
 *
 * @see Runnable
 */
public interface Job {
  void run() throws Exception;
}
