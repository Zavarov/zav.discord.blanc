/*
 * Copyright (c) 2020 Zavarov
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

package vartas.discord.blanc.mock;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import vartas.discord.blanc.command.Command;
import vartas.discord.blanc.command.CommandBuilder;
import vartas.discord.blanc.parser.Argument;
import vartas.discord.blanc.parser.Parser;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class CommandBuilderMock extends CommandBuilder {
    public Table<String, List<? extends Argument>, Command> commandTable = HashBasedTable.create();

    public CommandBuilderMock(@Nonnull Parser parser, @Nonnull String globalPrefix) {
        super((x,y) -> null, new ShardMock(), parser, globalPrefix);
    }

    @Override
    protected Optional<Command> build(String name, List<? extends Argument> arguments, List<String> flags) {
        return Optional.ofNullable(commandTable.get(name, arguments));
    }

    @Override
    public CommandBuilder getRealThis() {
        return this;
    }
}
