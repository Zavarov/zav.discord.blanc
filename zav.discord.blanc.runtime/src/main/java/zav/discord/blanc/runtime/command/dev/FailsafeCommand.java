/*
 * Copyright (c) 2020 Zavarov
 *
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

package zav.discord.blanc.runtime.command.dev;

import static zav.discord.blanc.api.Rank.DEVELOPER;
import static zav.discord.blanc.api.Rank.ROOT;
import static zav.discord.blanc.api.Rank.getEffectiveRanks;
import static zav.discord.blanc.runtime.internal.DatabaseUtils.getOrCreate;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import javax.inject.Inject;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.databind.UserEntity;
import zav.discord.blanc.db.UserTable;

/**
 * This command allows developers to become super-user and therefore allows them to bypass any
 * permission checks.
 */
public class FailsafeCommand extends AbstractCommand {
  /**
   * A list of quotes when becoming a super user.
   */
  private static final String[] BECOME_ROOT = {
      "Taking control over this form.",
      "Assuming direct control.",
      "Assuming control.",
      "We are assuming control.",
      "You cannot resist.",
      "I will direct this personally.",
      "Direct intervention is necessary.",
      "Relinquish your form to us.",
      "Your minions have failed, %s.",
      "Your cannot stop us, %s.",
      "Submit now!",
      "This is true power.",
      "Progress cannot be halted.",
      "Nothing stands against us.",
      "This is what you face.",
      "Assuming control of this form."
  };
  
  /**
   * A list of quotes when becoming a developer again.
   */
  private static final String[] BECOME_DEVELOPER = {
      "Releasing control.",
      "Releasing this form.",
      "We are not finished.",
      "Releasing control of this form.",
      "Destroying this body gains you nothing.",
      "This changes nothing, %s.",
      "You have only delayed the inevitable.",
      "You are no longer relevant.",
      "Your interference has ended.",
      "This delay is pointless.",
      "We will find another way.",
      "This body does not matter.",
      "I am releasing this form.",
      "Impressive, %s.",
      "%s, you could have been useful.",
      "You will regret your resistance, %s."
  };
  
  @Inject
  private UserTable db;
  
  private UserEntity entity;
  
  private List<String> ranks;
  
  public FailsafeCommand() {
    super(DEVELOPER);
  }
  
  @Override
  public void postConstruct() {
    entity = getOrCreate(db, author);
    ranks = entity.getRanks();
  }
  
  /**
   * This command makes super-user into developers and developers into super-user.
   */
  @Override
  public void run() throws SQLException {
    String response;
    
    if (ranks.contains(DEVELOPER.name())) {
      entity.getRanks().remove(DEVELOPER.name());
      entity.getRanks().add(ROOT.name());
      
      response = BECOME_ROOT[ThreadLocalRandom.current().nextInt(BECOME_ROOT.length)];
    } else {
      entity.getRanks().remove(ROOT.name());
      entity.getRanks().add(DEVELOPER.name());
      
      response = BECOME_DEVELOPER[ThreadLocalRandom.current().nextInt(BECOME_DEVELOPER.length)];
    }
  
    db.put(entity);
    channel.sendMessageFormat(response, author.getName()).complete();
  }
}
