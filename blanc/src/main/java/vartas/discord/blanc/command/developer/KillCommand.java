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

package vartas.discord.blanc.command.developer;

import net.dv8tion.jda.core.entities.Message;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.command.entity._ast.ASTEntityType;

import java.util.List;

/**
 * This command terminates the whole instance by halting all threads.
 */
public class KillCommand extends KillCommandTOP{
    public KillCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * Sends a shutdown request to the program core.
     */
    @Override
    public void run(){
        //Don't wait for all commands to finish while still being in a command.
        new Thread(() -> {
            environment.shutdown().run();
            System.exit(0);
        }).start();
    }
}
