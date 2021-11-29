package zav.discord.blanc.job;

import java.util.concurrent.Callable;

/**
 * This interface should be implement by any class that should be executed by an asynchronous thread
 * within the application.<br>
 * It is heavily inspired by both the {@link Callable} and {@link Runnable} interface.
 *
 * @see Runnable
 * @see Callable
 */
public interface Job {
  void run() throws Exception;
}
