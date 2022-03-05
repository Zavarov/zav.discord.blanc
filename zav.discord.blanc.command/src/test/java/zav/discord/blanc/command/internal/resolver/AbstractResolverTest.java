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

package zav.discord.blanc.command.internal.resolver;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.List;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import zav.discord.blanc.api.Parameter;
import zav.discord.blanc.api.Rank;
import zav.discord.blanc.command.internal.ParameterModule;

public abstract class AbstractResolverTest {
  
  protected final String string = "string";
  protected final long number = 12345L;
  protected final Rank rank = Rank.ROOT;
  
  protected @Mock JDA jda;
  protected @Mock Guild guild;
  protected @Mock Member member;
  protected @Mock TextChannel textChannel;
  protected @Mock Role role;
  protected @Mock User user;
  protected @Mock Message message;
  protected @Mock Parameter parameter;
  
  protected Injector injector;
  
  @BeforeEach
  public void setUp() {
    injector = Guice.createInjector(new ParameterModule(message, List.of(parameter)));
  }
}
