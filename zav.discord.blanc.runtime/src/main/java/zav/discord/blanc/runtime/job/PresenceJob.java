/*
 * Copyright (c) 2021 Zavarov.
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
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.managers.Presence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zav.discord.blanc.runtime.internal.BlancModule;

public class PresenceJob implements Runnable {
  private static final Logger LOGGER = LogManager.getLogger(PresenceJob.class);
  
  private final Presence self;
  
  private final List<String> statusMessages;
  
  public PresenceJob(Presence self) throws Exception {
    this.self = self;
    
    ObjectMapper om = new ObjectMapper();
    String[] messages = om.readValue(BlancModule.class.getClassLoader().getResourceAsStream("status.json"), String[].class);
    
    LOGGER.info("{} status messages have been read", messages.length);
    statusMessages = List.of(messages);
  }
  
  @Override
  public void run() {
    int index = ThreadLocalRandom.current().nextInt(statusMessages.size());
    String statusMessage = statusMessages.get(index);
  
  
    LOGGER.info("Change activity to '{}'.", statusMessage);
    self.setActivity(Activity.playing(statusMessage));
  }
}
