package vartas.discord.bot.io.permission.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import vartas.discord.bot.io.permission.PermissionConfiguration;
import vartas.discord.bot.io.permission.PermissionType;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/*
 * Copyright (C) 2019 Zavarov
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
public class PermissionPrettyPrinter {
    protected IndentPrinter printer;

    public PermissionPrettyPrinter(IndentPrinter printer){
        this.printer = printer;
    }

    public String prettyprint(PermissionConfiguration config){
        printer.clearBuffer();

        printer.addLine("permission {");

        printPermission(config);

        printer.addLine("}");

        return printer.getContent();
    }

    private void printPermission(PermissionConfiguration config){
        for(Map.Entry<Long, Collection<PermissionType>> entry : config.getPermissions().asMap().entrySet())
            printPermission(entry.getKey(), entry.getValue());
    }

    private void printPermission(long id, Collection<PermissionType> types){
        printer.print(String.format("user : %d L has rank ", id));

        Iterator<PermissionType> iterator = types.iterator();

        while(iterator.hasNext()){
            printer.print(iterator.next().getMontiCoreName());
            if(iterator.hasNext())
                printer.print(",");
        }

        printer.println();
    }
}
