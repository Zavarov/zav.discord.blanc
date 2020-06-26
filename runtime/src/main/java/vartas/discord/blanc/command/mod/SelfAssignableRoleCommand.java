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

package vartas.discord.blanc.command.mod;

import com.google.common.base.Preconditions;
import vartas.discord.blanc.Shard;

import java.util.Optional;

/**
 * This command groups roles together so that only one of them can be self-assigned at a time.
 */
public class SelfAssignableRoleCommand extends SelfAssignableRoleCommandTOP{
    @Override
    public void run(){
        //The role is in this group -> Ungroup
        if(getRole().getGroup().map(group -> group.equals(getGroup())).orElse(false)){
            getRole().setGroup(Optional.empty());
            get$TextChannel().send("Ungrouped "+role.getName()+".");
        //The role is in a different group
        }else if(getRole().isPresentGroup()){
            get$TextChannel().send("The role is already grouped under "+getRole().getGroup().get()+".");
        //The person executing this command can't interact with the role
        }else if(!get$Guild().canInteract(get$Author(), getRole())){
            get$TextChannel().send("You need to be able to interact with roles you want to be self-assignable.");
        //This bot can't interact with the role
        }else if(!get$Guild().canInteract(get$Guild().getSelfMember(), getRole())){
            get$TextChannel().send("I need to be able to interact with roles you want to be self-assignable.");
        //Everything OK, make role self-assignable
        }else{
            getRole().setGroup(getGroup());
            get$TextChannel().send("The role "+role.getName()+" has been grouped under "+getGroup()+".");
        }
        Shard.write(get$Guild());
    }
}
