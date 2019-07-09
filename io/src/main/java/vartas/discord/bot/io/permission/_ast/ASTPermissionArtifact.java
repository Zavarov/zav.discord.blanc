package vartas.discord.bot.io.permission._ast;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import vartas.discord.bot.io.permission.PermissionType;

import java.util.List;

/*
 * Copyright (C) 2019 Zavarov
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
public class ASTPermissionArtifact extends ASTPermissionArtifactTOP{
    protected ASTPermissionArtifact(){
        super();
    }
    protected ASTPermissionArtifact(List<ASTPermission> permissionList){
        super(permissionList);
    }

    public Multimap<Long, PermissionType> getPermissions(){
        Multimap<Long, PermissionType> permissions = HashMultimap.create();

        for(ASTPermission permission : getPermissionList())
            for(ASTPermissionType type : permission.getPermissionTypeList())
                permissions.put(permission.getBasicLongLiteral().getValue(), type.getPermissionType());

        return permissions;
    }
}
