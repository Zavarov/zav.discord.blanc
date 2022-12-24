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

package zav.discord.blanc.runtime.job;

import java.io.IOException;
import java.security.SecureRandom;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Shard;
import zav.discord.blanc.databind.Status;

/**
 * Repeatable job which updates the status message of the user account associated with this
 * application.
 */
public class PresenceJob implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(PresenceJob.class);
  private static final SecureRandom RANDOMIZER = new SecureRandom();
  
  private final Client self;
  
  private final Status status;
  
  /**
   * Reads the status messages from disk.
   *
   * @param self The presence of this application within a given shard.
   * @throws IOException If the file containing the status messages couldn't be read.
   */
  public PresenceJob(Client self) throws IOException {
    this.self = self;
    this.status = Status.read(PresenceJob.class.getClassLoader(), "Status.json");
    
    LOGGER.info("{} status messages have been read.", status.getMessages().size());
  }
  
  @Override
  public void run() {
    int index = RANDOMIZER.nextInt(status.getMessages().size());
    String statusMessage = status.getMessages().get(index);
  
  
    LOGGER.info("Change activity to '{}'.", statusMessage);
    for (Shard shard : self.getShards()) {
      shard.getJda().getPresence().setActivity(Activity.playing(statusMessage));
    }
  }
}
