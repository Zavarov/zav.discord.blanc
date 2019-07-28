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

import net.dv8tion.jda.core.entities.Message;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.command.entity._ast.ASTEntityType;

import java.util.List;
import java.util.Optional;

/**
 * This command groups roles together so that only one of them can be self-assigned at a time.
 */
public class SelfAssignableRoleCommand extends SelfAssignableRoleCommandTOP{
    public SelfAssignableRoleCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    @Override
    public void run(){
        Optional<String> group = config.getTag(role);

        //The role is grouped under this tag -> Untag
        if(group.isPresent() && group.get().equals(tag)){
            config.untag(role);
            communicator.send(channel, "Untagged "+role.getName()+".");
        //The role is grouped under a different tag
        }else if(group.isPresent()){
            communicator.send(channel, "The role is already tagged under "+group.get()+".");
        //The person executing this command can't interact with the role
        }else if(!guild.getMember(author).canInteract(role)){
            communicator.send(channel, "You need to be able to interact with roles you want to be self-assignable.");
        //This bot can't interact with the role
        }else if(!member.canInteract(role)){
            communicator.send(channel, "I need to be able to interact with roles you want to be self-assignable.");
        }else{
            config.tag(tag, role);
            communicator.send(channel, "The role "+role.getName()+" has been tagged under "+tag+".");
        }
    }
}
