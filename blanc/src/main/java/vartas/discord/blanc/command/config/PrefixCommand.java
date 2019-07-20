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

import net.dv8tion.jda.core.entities.Message;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.command.entity._ast.ASTEntityType;

import java.util.List;

/**
 * This command shows the custom prefix of this server.
 */
public class PrefixCommand extends PrefixCommandTOP{
    public PrefixCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * Prints the custom prefix, if it is set.
     */
    @Override
    public void run(){
        StringBuilder builder = new StringBuilder();
        if(config.getPrefix().isPresent()) {
            builder.append("The custom prefix in this guild is:\n");
            builder.append(config.getPrefix().get());
        }else{
            builder.append("This guild doesn't have a custom prefix.");
        }
        communicator.send(channel, builder.toString());
    }
}