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

import vartas.discord.blanc.parser.Argument;
import vartas.discord.blanc.parser.IntermediateCommand;

import java.util.List;
import java.util.Optional;

public class IntermediateCommandMock implements IntermediateCommand {
    public String prefix;
    public String name;
    public List<Argument> arguments;
    public List<String> flags;

    public IntermediateCommandMock(String prefix, String name, List<Argument> arguments){
        this.prefix = prefix;
        this.name = name;
        this.arguments = arguments;
    }

    public void setPrefix(String prefix){
        this.prefix = prefix;
    }

    @Override
    public Optional<String> getPrefix() {
        return Optional.ofNullable(prefix);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getFlags() {
        return flags;
    }

    @Override
    public List<Argument> getArguments() {
        return arguments;
    }
}
