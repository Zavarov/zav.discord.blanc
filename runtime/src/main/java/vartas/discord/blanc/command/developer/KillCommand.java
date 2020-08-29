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

package vartas.discord.blanc.command.developer;

import vartas.discord.blanc.Killable;
import vartas.discord.blanc.KillableTOP;
import vartas.discord.blanc.Main;
import vartas.discord.blanc.visitor.ArchitectureInheritanceVisitor;

/**
 * This command terminates the whole instance by halting all threads.
 */
public class KillCommand extends KillCommandTOP implements ArchitectureInheritanceVisitor {
    @Override
    public void run(){
        Main.CLIENT.accept(this);

        //Give commands time to finish
        try{
            Thread.sleep(5000);
        }catch(InterruptedException ignored){}

        System.exit(0);
    }

    @Override
    public void visit(KillableTOP node){
        node.shutdown();
    }
}
