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

import com.google.inject.AbstractModule;
import com.google.inject.MembersInjector;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import java.lang.reflect.Field;
import java.util.List;
import net.dv8tion.jda.api.entities.Message;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.eclipse.jdt.annotation.NonNullByDefault;
import zav.discord.blanc.api.Argument;
import zav.discord.blanc.api.Parameter;

/**
 * The Guice module responsible for initializing fields annotated with the {@link Argument}
 * annotation.
 */
@NonNullByDefault
public class ParameterModule extends AbstractModule {
  
  private final Message message;
  private final List<? extends Parameter> params;
  
  public ParameterModule(Message message, List<? extends Parameter> params) {
    this.message = message;
    this.params = params;
  }
  
  @Override
  protected void configure() {
    bindListener(Matchers.any(), new TypeListener() {
      @Override
      public <I> void hear(TypeLiteral<I> type, TypeEncounter<I> encounter) {
        Class<?> clazz = type.getRawType();
        
        while (clazz != null) {
          for (Field field : FieldUtils.getFieldsWithAnnotation(clazz, Argument.class)) {
            encounter.register(new ParameterInjector<>(field));
          }
          clazz = clazz.getSuperclass();
        }
      }
    });
  }
  
  private class ParameterInjector<T> implements MembersInjector<T> {
    private final Field field;
    private final Argument argument;
    
    private ParameterInjector(Field field) {
      this.field = field;
      this.argument = field.getAnnotation(Argument.class);
    }
  
  
    @Override
    public void injectMembers(T instance) {
      try {
        Class<?> targetType = field.getType();
        
        Object value = null;
        
        // Try to resolve when the index specifies a valid parameter.
        if (argument.index() < params.size()) {
          value = ParameterResolver.resolve(targetType, params.get(argument.index()), message);
        }
        
        // Try to resolve when the index is out of range (and therefore value is still null) and if
        // the useDefault flag is set.
        if (argument.index() >= params.size() && argument.useDefault()) {
          value = ParameterResolver.getDefault(targetType, message);
        }
        
        FieldUtils.writeField(field, instance, value, true);
      } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
