/*
 * Copyright (C) 2017 u/Zavarov
 *
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

package vartas.discordbot.command;

/**
 * This exception is thrown when the search for a Discord entity returned more
 * than one result.
 * @author u/Zavarov
 */
public class AmbiguousNameException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    /**
     * @param <Q> the type of the entity.
     * @param entity the entity that caused an exception.
     */
    public <Q> AmbiguousNameException(Q entity){
        super(String.format("The entity %s yielded more than one result.",entity.toString()));
    }
}
