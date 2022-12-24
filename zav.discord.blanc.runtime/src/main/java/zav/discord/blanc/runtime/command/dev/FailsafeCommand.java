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

package zav.discord.blanc.runtime.command.dev;

import java.security.SecureRandom;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.command.AbstractCommand;
import zav.discord.blanc.command.CommandManager;
import zav.discord.blanc.databind.Rank;
import zav.discord.blanc.databind.UserEntity;

/**
 * This command allows developers to become super-user and therefore allows them to bypass any
 * permission checks.
 */
public class FailsafeCommand extends AbstractCommand {
  private static final SecureRandom RANDOMIZER = new SecureRandom();
  /**
   * A list of quotes when becoming a super-user.
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

  private final User author;
  private final SlashCommandEvent event;
  
  /**
   * Creates a new instance of this command.
   *
   * @param event The event triggering this command.
   * @param manager The manager instance for this command.
   */
  public FailsafeCommand(SlashCommandEvent event, CommandManager manager) {
    super(manager);
    this.author = event.getUser();
    this.event = event;
  }
  
  @Override
  public Rank getRequiredRank() {
    return Rank.DEVELOPER;
  }

  /**
   * This command makes super-user into developers and developers into super-user.
   */
  @Override
  public void run() {
    // Persist entity modifications
    UserEntity entity = UserEntity.find(author);
    String response;

    if (entity.getRanks().contains(Rank.DEVELOPER)) {
      entity.getRanks().remove(Rank.DEVELOPER);
      entity.getRanks().add(Rank.ROOT);

      response = BECOME_ROOT[RANDOMIZER.nextInt(BECOME_ROOT.length)];
    } else {
      entity.getRanks().remove(Rank.ROOT);
      entity.getRanks().add(Rank.DEVELOPER);

      response = BECOME_DEVELOPER[RANDOMIZER.nextInt(BECOME_DEVELOPER.length)];
    }

    entity.merge();

    event.replyFormat(response, author.getAsMention()).complete();
  }
}
