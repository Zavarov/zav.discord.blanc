package zav.discord.blanc.command.internal;

import com.google.inject.AbstractModule;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.command.CommandManager;

/**
 * The Guice module for all (private) commands.
 */
public class CommandModule extends AbstractModule {
  
  private final CommandManager manager;
  private final SlashCommandEvent event;
  
  /**
   * Creates a new instance of this class. For each command, a new module is created.
   *
   * @param client The global application instance.
   * @param event The event triggering the creation of this module.
   */
  public CommandModule(Client client, SlashCommandEvent event) {
    this.manager = new CommandManager(client, event);
    this.event = event;
  }

  @Override
  public void configure() {
    bind(CommandManager.class).toInstance(manager);
    bind(SlashCommandEvent.class).toInstance(event);
  }
}
