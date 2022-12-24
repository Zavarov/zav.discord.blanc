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

package zav.discord.blanc.runtime.internal.validator;

import java.util.function.Predicate;

/**
 * Classes implementing this interface provide the capability to check, whether an entity is still
 * up-to-date. Outdated entities are then automatically removed from the database.
 *
 * @param <T> The entity type this validator can accept.
 */
public interface Validator<T> extends Predicate<T> {

}
