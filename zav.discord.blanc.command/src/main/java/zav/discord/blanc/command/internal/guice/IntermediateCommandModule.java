package zav.discord.blanc.command.internal.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import zav.discord.blanc.command.parser.IntermediateCommand;

import java.util.List;
import java.util.Optional;

public class IntermediateCommandModule extends AbstractModule {
  private final IntermediateCommand cmd;
  public IntermediateCommandModule(IntermediateCommand cmd) {
    this.cmd = cmd;
  }
  
  @Override
  protected void configure() {
    bind(List.class).annotatedWith(Names.named("args")).toInstance(cmd.getArguments());
    bind(List.class).annotatedWith(Names.named("flags")).toInstance(cmd.getFlags());
    bind(String.class).annotatedWith(Names.named("name")).toInstance(cmd.getName());
    bind(Optional.class).annotatedWith(Names.named("prefix")).toInstance(cmd.getPrefix());
  }
}
