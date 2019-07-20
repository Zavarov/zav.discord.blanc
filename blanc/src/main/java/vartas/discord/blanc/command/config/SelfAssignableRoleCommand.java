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
package vartas.discord.blanc.command.config;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.api.message.InteractiveMessage;
import vartas.discord.bot.command.entity._ast.ASTEntityType;

import java.util.List;

/**
 * This command returns a list of all grouped roles in the server.
 */
public class SelfAssignableRoleCommand extends SelfAssignableRoleCommandTOP {
    public SelfAssignableRoleCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * Prints all selfassignable roles and their respective tags, if they exist.
     */
    @Override
    public void run(){
        Multimap<String,Role> map = config.getTags(guild);
        if(map.isEmpty()){
            communicator.send(channel,"There are no selfassignable roles in this guild.");
        }else {

            InteractiveMessage.Builder builder = new InteractiveMessage.Builder(channel, author, communicator);

            builder.addDescription("All selfassignable roles");
            map.asMap().forEach((key, value) -> {
                List<Role> roles = Lists.newArrayList(value);
                for (int i = 0; i < roles.size(); i += 10) {
                    builder.addLine(String.format("`Roles for %s`", key));
                    for (int j = i; j < Math.min(i + 10, value.size()); ++j) {
                        builder.addLine(roles.get(j).getAsMention());
                    }
                    builder.nextPage();
                }
            });
            communicator.send(builder.build());
        }
    }
}