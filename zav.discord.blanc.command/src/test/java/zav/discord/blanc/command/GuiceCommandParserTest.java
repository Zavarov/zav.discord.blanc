package zav.discord.blanc.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.Optional;
import javax.inject.Inject;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Client;
import zav.discord.blanc.api.Command;

/**
 * Test case to check whether the arguments are correctly injected into the constructor.
 */
@ExtendWith(MockitoExtension.class)
public class GuiceCommandParserTest {
  
  @Mock Client client;
  @Mock SlashCommandEvent event;
  @Mock Member member;
  @Mock TextChannel textChannel;
  GuiceCommandParser parser;
  Injector injector;
  
  /**
   * Initializes two commands with name {@code zav.discord.guild} and {@code zav.discord.private},
   * bound to {@link #GuildCommand} and {@link #PrivateCommand} respectively.
   */
  @BeforeEach
  public void setUp() {
    Commands.bind("zav.discord.guild", GuildCommand.class);
    Commands.bind("zav.discord.private", PrivateCommand.class);
    injector = Guice.createInjector();
    parser = new GuiceCommandParser(client, injector);
    
    when(event.getName()).thenReturn("zav");
    when(event.getSubcommandGroup()).thenReturn("discord");
  }
  
  @AfterEach
  public void tearDown() {
    Commands.clear();
  }
  
  @Test
  public void testParseGuildCommand() {
    when(event.getSubcommandName()).thenReturn("guild");
    when(event.isFromGuild()).thenReturn(true);
    when(event.getMember()).thenReturn(member);
    when(event.getTextChannel()).thenReturn(textChannel);
    
    Optional<Command> command = parser.parse(event);
    
    assertThat(command).isPresent();
    assertThat(command.get()).isInstanceOf(GuildCommand.class);
  }
  
  @Test
  public void testParseUnknownCommand() {
    Optional<Command> command = parser.parse(event);
    
    assertThat(command).isEmpty();
  }
  
  @Test
  public void testParsePrivateCommand() {
    when(event.getSubcommandName()).thenReturn("private");
    
    Optional<Command> command = parser.parse(event);
    
    assertThat(command).isPresent();
    assertThat(command.get()).isInstanceOf(PrivateCommand.class);
  }
  
  private static final class GuildCommand extends AbstractGuildCommand {
    @Inject
    protected GuildCommand(GuildCommandManager manager) {
      super(manager);
    }

    @Override
    public void run() {}
  }
  
  private static final class PrivateCommand extends AbstractCommand {
    @Inject
    protected PrivateCommand(CommandManager manager) {
      super(manager);
    }

    @Override
    public void run() {}
  }

}
