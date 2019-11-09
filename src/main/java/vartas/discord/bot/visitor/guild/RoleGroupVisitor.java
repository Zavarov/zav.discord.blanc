/*
 * Copyright (c) 2019 Zavarov
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

package vartas.discord.bot.visitor.guild;

import net.dv8tion.jda.api.entities.Role;
import vartas.discord.bot.entities.guild.RoleGroup;

import java.util.Set;

public interface RoleGroupVisitor {
    default void visit(RoleGroup group){}

    default void traverse(RoleGroup group) {
        group.accept(this);
    }

    default void handle(RoleGroup group){
        visit(group);
        traverse(group);
    }

    default void visitRoles(String key, Set<Role> values){}

    default void traverseRoles(String key, Set<Role> values){
        for(Role value : values)
            handle(key, value);
    }

    default void handleRoles(String key, Set<Role> values){
        visitRoles(key, values);
        traverseRoles(key, values);
    }

    default void visit(String key, Role value){}

    default void traverse(String key, Role value){}

    default void handle(String key, Role value){
        visit(key, value);
        traverse(key, value);
    }
}
