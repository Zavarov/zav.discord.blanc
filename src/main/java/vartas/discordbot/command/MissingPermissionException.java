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

import java.util.Collection;
import net.dv8tion.jda.core.Permission;

/**
 * This exception is thrown when a user attempts to execute a mod command without the required permissions.
 * @author u/Zavarov
 */
public class MissingPermissionException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    /**
     * @param permissions the permissions that the user misses.
     */
    public MissingPermissionException(Collection<Permission> permissions){
        super(String.format("You need the following permissions to use the command:\n%s",permissions.stream().map(o -> o.getName()).reduce( (u,v) -> u+"\n"+v).get()));
    }
}
