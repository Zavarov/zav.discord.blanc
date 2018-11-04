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

import vartas.xml.XMLServer;

/**
 * This class is the body of all commands that require a guild.
 * @author u/Zavarov
 */
public abstract class GuildCommand extends Command{
    /**
     * The configuration file associated with the guild.
     */
    protected XMLServer server;
    /**
     * Before the execution, the configuration file of the server, the command
     * was executing in, will be retrieved.
     * @throws CommandRequiresGuildException if the message is not from inside a guild.
     */
    @Override
    public void checkRequirements() throws CommandRequiresGuildException{
        super.requiresGuild();
        server = bot.getServer(message.getGuild());
        super.run();
    }
}
