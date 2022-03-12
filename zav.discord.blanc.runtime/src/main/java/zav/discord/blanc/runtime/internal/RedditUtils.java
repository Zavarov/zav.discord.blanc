/*
 * Copyright (c) 2022 Zavarov.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package zav.discord.blanc.runtime.internal;

import com.google.inject.Injector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.jrc.client.Client;
import zav.jrc.client.Duration;
import zav.jrc.client.FailedRequestException;

/**
 * Utility class for setting up the Reddit client.
 */
public class RedditUtils {
  private static final Logger LOGGER = LogManager.getLogger(RedditUtils.class);
  
  /**
   * Initializes the Reddit client and requests a fresh access token. The duration of the token is
   * temporary. Furthermore, a shutdown hook is added, invalidating hte access token when terminated
   * the application.
   *
   * @param injector The injector used by the Reddit client.
   * @throws FailedRequestException If no access token could be requested.
   */
  public static void init(Injector injector) throws FailedRequestException {
    Client reddit = injector.getInstance(Client.class);
    reddit.login(Duration.TEMPORARY);
  
    // Revoke the (temporary) access token
    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      try {
        reddit.logout();
      } catch (Exception e) {
        LOGGER.error(e.getMessage(), e);
      }
    }));
  }
}
