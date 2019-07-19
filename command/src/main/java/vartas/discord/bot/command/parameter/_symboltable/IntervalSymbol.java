package vartas.discord.bot.command.parameter._symboltable;

import vartas.discord.bot.command.entity._ast.ASTIntervalType;
import vartas.reddit.chart.line.AbstractChart.Interval;

import java.util.Locale;
import java.util.Optional;

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
public class IntervalSymbol extends IntervalSymbolTOP{
    protected ASTIntervalType ast;

    public IntervalSymbol(String name) {
        super(name);
    }

    public void setValue(ASTIntervalType ast){
        this.ast = ast;
    }

    public ASTIntervalType getValue(){
        return ast;
    }

    public Optional<Interval> resolve(){
        try{
            return Optional.of(Interval.valueOf(ast.getInterval().toUpperCase(Locale.ENGLISH)));
        //Thrown when there is no interval with the specified name
        }catch(IllegalArgumentException e){
            return Optional.empty();
        }
    }
}
