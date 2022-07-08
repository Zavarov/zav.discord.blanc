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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zav.discord.blanc.api.Client;

/**
 * Repeatable job which updates the status message of the user account associated with this
 * application.
 */
public class PresenceJob implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(PresenceJob.class);
  private static final SecureRandom RANDOMIZER = new SecureRandom();
  
  private final Client self;
  
  private final List<String> statusMessages;
  
  /**
   * Reads the status messages from disk.
   *
   * @param self The presence of this application within a given shard.
   * @throws IOException If the file containing the status messages couldn't be read.
   */
  public PresenceJob(Client self) throws IOException {
    this.self = self;
    
    ObjectMapper om = new ObjectMapper();
    InputStream is = PresenceJob.class.getClassLoader().getResourceAsStream("Status.json");
    String[] messages = om.readValue(is, String[].class);
    
    LOGGER.info("{} status messages have been read.", messages.length);
    statusMessages = List.of(messages);
  }
  
  @Override
  public void run() {
    int index = RANDOMIZER.nextInt(statusMessages.size());
    String statusMessage = statusMessages.get(index);
  
  
    LOGGER.info("Change activity to '{}'.", statusMessage);
    for (JDA shard : self.getShards()) {
      shard.getPresence().setActivity(Activity.playing(statusMessage));
    }
  }
}
