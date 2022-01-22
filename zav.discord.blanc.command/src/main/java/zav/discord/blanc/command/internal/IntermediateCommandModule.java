package zav.discord.blanc.command.internal;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import java.util.List;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.api.Parameter;
import zav.discord.blanc.api.command.IntermediateCommand;

/**
 * Injector module for all commands.<br>
 * It provides all arguments that were used to create the command for injection:
 * <pre>
 *   &#64;Inject &#64;Named("params")
 *   List&lt;String&gt params;
 *
 *   &#64;Inject &#64;Named("flags")
 *   List&lt;String&gt flags;
 *
 *   &#64;Inject &#64;Named("name")
 *   String name;
 *
 *   &#64;Inject &#64;Named("prefix")
 *   String prefix;
 * </pre>
 */
@NonNullByDefault
public class IntermediateCommandModule extends AbstractModule {
  private final IntermediateCommand cmd;
  
  public IntermediateCommandModule(IntermediateCommand cmd) {
    this.cmd = cmd;
  }
  
  @Override
  protected void configure() {
    bind(new TypeLiteral<List<? extends Parameter>>(){})
          .annotatedWith(Names.named("params"))
          .toInstance(cmd.getParameters());
    
    bind(new TypeLiteral<List<String>>(){})
          .annotatedWith(Names.named("flags"))
          .toInstance(cmd.getFlags());
    
    
    cmd.getPrefix().ifPresent(prefix ->
        bind(String.class).annotatedWith(Names.named("prefix")).toInstance(prefix)
    );
  
    bind(String.class).annotatedWith(Names.named("name")).toInstance(cmd.getName());
  }
}
