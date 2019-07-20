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

package vartas.discord.blanc.command.developer;

import com.google.common.collect.Lists;
import net.dv8tion.jda.core.entities.Message;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.api.listener.LogListener;
import vartas.discord.bot.api.message.InteractiveMessage;
import vartas.discord.bot.command.entity._ast.ASTEntityType;

import java.util.ArrayList;
import java.util.List;

/**
 * This command allows the user access to the internal log.
 */
public class LogCommand extends LogCommandTOP{
    public LogCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * Retrieve the entries from the log and submit them.
     */
    @Override
    public void run(){
        InteractiveMessage.Builder output = new InteractiveMessage.Builder(channel, author, communicator);
        List<Object> data = new ArrayList<>(LogListener.MEMORY);
        //The first entry in the queue is the oldest entry.
        data = Lists.reverse(data);
        output.addLines(data, 10);
        communicator.send(output.build());
    }
}
