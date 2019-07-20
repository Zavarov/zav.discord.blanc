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

package vartas.discord.blanc.command.base;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.command.entity._ast.ASTEntityType;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

/**
 * The command that assigns self-assignable roles to the user.
 */
public class AssignCommand extends AssignCommandTOP{
    /**
     * A mutex that guarantees that an assignment has been finished, before
     * another one can be started. This prevents a race condition, where 
     * multiple roles are assigned at once.
     */
    protected final static Semaphore MUTEX = new Semaphore(1);

    public AssignCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * First, the roles are received. If the role is self-assignable, it is added
     * to the user who requested it, if the person doesn't already have the role.
     * Otherwise it is removed.
     */
    @Override
    public void run(){
        MUTEX.acquireUninterruptibly();

        try {
            Optional<Role> optional = roleSymbol.resolve(source);
            if (optional.isPresent()) {
                Role role = optional.get();

                Optional<String> tag = config.getTag(role);
                if(tag.isPresent()){
                    if(member.getRoles().contains(role)){
                        communicator.send(guild.getController().removeSingleRoleFromMember(member, role), o -> MUTEX.release(), o -> MUTEX.release());
                        communicator.send(channel, String.format("Removed role %s.", role.getName()));
                    }else{
                        Collection<Role> others = config.getTags(guild).get(tag.get());
                        Collection<Role> conflictingRoles = member
                                .getRoles()
                                .stream()
                                .filter(others::contains)
                                .collect(Collectors.toList());

                        if(conflictingRoles.isEmpty()){
                            communicator.send(guild.getController().addSingleRoleToMember(member, role), o -> MUTEX.release(), o -> MUTEX.release());
                        }else{
                            communicator.send(guild.getController().modifyMemberRoles(member, Collections.singleton(role), conflictingRoles), o -> MUTEX.release(), o -> MUTEX.release());
                        }
                        communicator.send(channel, String.format("Added role %s.", role.getName()));
                    }
                }else{
                    communicator.send(channel, "The specified role isn't self-assignable.");
                    MUTEX.release();
                }

            } else {
                communicator.send(channel, "The specified role couldn't be resolved.");
                MUTEX.release();
            }
        //Just in case so we don't end up in a deadlock
        }catch(RuntimeException e){
            communicator.send(channel, e.getMessage());
            MUTEX.release();
        }
    }
}
