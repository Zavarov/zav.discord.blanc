package zav.discord.blanc.command.internal;

import com.google.inject.AbstractModule;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.command.CommandManager;
import zav.discord.blanc.command.GuildCommandManager;

/**
 * The Guice module for all guild commands.
 */
public class GuildCommandModule extends AbstractModule {
  
  private final GuildCommandManager manager;
  private final SlashCommandEvent event;
  
  /**
   * Creates a new manager instance. A new instance is created for each command.
   *
   * @param client The Discord client.
   * @param event The event from which the active command was created.
   */
  public GuildCommandModule(Client client, SlashCommandEvent event) {
    this.manager = new GuildCommandManager(client, event);
    this.event = event;
  }

  @Override
  public void configure() {
    bind(CommandManager.class).toInstance(manager);
    bind(GuildCommandManager.class).toInstance(manager);
    bind(SlashCommandEvent.class).toInstance(event);
  }

}
