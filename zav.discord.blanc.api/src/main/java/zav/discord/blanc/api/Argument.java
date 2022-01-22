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

package zav.discord.blanc.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.eclipse.jdt.annotation.NonNullByDefault;

/**
 * This annotation is used inside commands to automatically deserialize the parameters into their
 * desired data types. Example:
 * <pre>
 * &#064;Argument(index = 0, useDefault = true)
 * Guild guild;
 *
 * &#064;Argument(index = 1)
 * TextChannel channel;
 * </pre>
 */
@NonNullByDefault
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Argument {
  /**
   * Specifies the index of the parameter which is deserialized. When out of range or the parameter
   * could not be transformed into an entity, the field is initialized with {@code null}.
   *
   * @return The index of the parameter which is used to initialize the annotated field.
   */
  int index();
  
  /**
   * When set to {@code true}, tries to derive a default value when the provided index is out of
   * range. For example, the default of a user is the message author.<br>
   * <b>NOTE:</b> It is still possible to get {@code null} values, even when this flag is
   * set. E.g. when the parameter could not be deserialized or when no default value exists.
   *
   * @return {@code true}, when default values should be used, when possible.
   */
  boolean useDefault() default false;
}
