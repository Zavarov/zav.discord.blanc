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

package vartas.discord.bot.api.command;

import de.monticore.symboltable.GlobalScope;
import net.dv8tion.jda.core.entities.Message;
import vartas.discord.bot.api.communicator.CommunicatorInterface;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 */
public abstract class AbstractCommandBuilder {
    protected CommunicatorInterface communicator;
    protected GlobalScope scope;

    public AbstractCommandBuilder(GlobalScope scope, CommunicatorInterface communicator){
        checkNotNull(scope);
        checkNotNull(communicator);

        this.scope = scope;
        this.communicator = communicator;
    }


    public abstract AbstractCommand build(String content, Message source);
}
