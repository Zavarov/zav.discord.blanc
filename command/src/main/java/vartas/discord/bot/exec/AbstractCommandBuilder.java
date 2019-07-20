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

package vartas.discord.bot.exec;

import net.dv8tion.jda.core.entities.Message;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.command.AbstractCommand;
import vartas.discord.bot.command.call._ast.ASTCallArtifact;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractCommandBuilder {
    protected ASTCallArtifact source;
    protected CommunicatorInterface communicator;
    protected Message context;

    protected Map<String, Supplier<? extends AbstractCommand>> commands;

    public AbstractCommandBuilder(){
        commands = new HashMap<>();
    }

    public AbstractCommandBuilder setSource(ASTCallArtifact source){
        this.source = source;
        return this;
    }

    public AbstractCommandBuilder setCommunicator(CommunicatorInterface communicator){
        this.communicator = communicator;
        return this;
    }

    public AbstractCommandBuilder setContext(Message context){
        this.context = context;
        return this;
    }

    public AbstractCommand build() throws IllegalArgumentException, IllegalStateException{
        checkNotNull(source);
        checkNotNull(communicator);
        checkNotNull(context);

        return commands.get(source.getQualifiedName()).get();
    }
}
