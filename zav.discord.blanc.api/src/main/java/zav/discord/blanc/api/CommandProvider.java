package zav.discord.blanc.api;

import java.util.Optional;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

/**
 * Provider class for all available commands. 
 */
public interface CommandProvider {
  /**
   * Creates the command corresponding to the given slash event. If no matching command is found,
   * {@link Optional#empty()} is returned. Each command is uniquely identified by its qualified
   * name.
   *
   * @param client The Discord instance.
   * @param event The event from which the command is created.
   * @return An optional containing the created command.
   */
  Optional<Command> create(Client client, SlashCommandEvent event);
}
