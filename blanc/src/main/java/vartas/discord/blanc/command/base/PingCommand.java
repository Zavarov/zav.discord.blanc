package vartas.discord.blanc.command.base;

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

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.command.entity._ast.ASTEntityType;

import java.util.List;
import java.util.function.Consumer;

/**
 * This command measures the time it takes to send a command and receiving
 * the confirmation from Discord.
 */
public class PingCommand extends PingCommandTOP implements Consumer<Message>{
    /**
     * The timestamp of when the first message was sent.
     */
    protected long time;

    public PingCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * Sends a message to discord and awaits a confirmation.
     */
    @Override
    public void run() {
        time = System.currentTimeMillis();
        MessageBuilder builder = new MessageBuilder().append("Response");
        communicator.send(channel, builder,this);
    }
    /**
     * The message was sent successfully and it is updated with the time it took.
     * @param message the message that was sent.
     */
    @Override
    public void accept(Message message) {
        communicator.send(message.editMessage("Response in "+(System.currentTimeMillis()-time)+"ms."));
    }
}
