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

package vartas.discord.bot;

import net.dv8tion.jda.api.entities.Message;

import java.util.function.Supplier;

public class TestCommandBuilder extends CommandBuilder{
    protected String prefix = "b.";
    protected Supplier<Command> supplier;
    public TestCommandBuilder(Supplier<Command> supplier){
        this.supplier = supplier;
    }

    @Override
    public Command build(String content, Message source) {
        return supplier.get();
    }
}
