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

package zav.discord.blanc.command.internal;

import static zav.discord.blanc.api.Constants.FLAGS;
import static zav.discord.blanc.api.Constants.NAME;
import static zav.discord.blanc.api.Constants.PARAMS;
import static zav.discord.blanc.api.Constants.PREFIX;

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import java.util.List;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.api.Parameter;
import zav.discord.blanc.command.IntermediateCommand;

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
          .annotatedWith(Names.named(PARAMS))
          .toInstance(cmd.getParameters());
    
    bind(new TypeLiteral<List<String>>(){})
          .annotatedWith(Names.named(FLAGS))
          .toInstance(cmd.getFlags());
    
    
    cmd.getPrefix().ifPresent(prefix ->
        bind(String.class).annotatedWith(Names.named(PREFIX)).toInstance(prefix)
    );
  
    bind(String.class).annotatedWith(Names.named(NAME)).toInstance(cmd.getName());
  }
}
