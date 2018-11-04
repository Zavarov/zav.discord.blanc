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

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.utils.PermissionUtil;
import org.apache.commons.collections4.CollectionUtils;

/**
 * The frame for a mod command.
 * In addition to a guild command, it also requires a certain set of permissions, in order to be executed.
 * @author u/Zavarov
 */
public abstract class ModCommand extends GuildCommand{
    /**
     * The set of permissions the person who called this command needs.
     */
    protected final Set<Permission> required;
    /**
     * @param required an array of the required permissions.
     */
    public ModCommand(Permission... required){
        this(Sets.newHashSet(required));
    }
    /**
     * @param required a collection of the required permissions.
     */
    public ModCommand(Set<Permission> required){
        super();
        this.required = required;
    }
    /**
     * Checks if the author has the required permissions.
     * @throws MissingPermissionException if the author doesn't have at least one of the required permissions.
     */
    @Override
    public void checkRequirements() throws MissingPermissionException{
        List<Permission> permissions = Permission.getPermissions(PermissionUtil.getEffectivePermission(message.getTextChannel(),message.getMember()));
        
        boolean isRoot = permission.getRanks(message.getAuthor()).contains(Rank.ROOT);
        boolean hasPermission = permissions.containsAll(required);
        
        if(!isRoot && !hasPermission){
            throw new MissingPermissionException(CollectionUtils.subtract(required,permissions));
        }
    }
    /**
     * @param supplier a supplier that checks if the author can interact with an entity.
     * @return true if the author is root or if the supplier returns true.
     */
    public boolean canInteract(Supplier<Boolean> supplier){
        boolean isRoot = permission.getRanks(message.getAuthor()).contains(Rank.ROOT);
        return isRoot || supplier.get();
    }
}