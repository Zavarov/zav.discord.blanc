package vartas.discord.bot.command.parameter._symboltable;

import vartas.discord.bot.command.entity._ast.ASTDateType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

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
public class DateSymbol extends DateSymbolTOP{
    protected SimpleDateFormat dateFormat;
    protected ASTDateType ast;

    public DateSymbol(String name) {
        super(name);

        dateFormat = new SimpleDateFormat("dd-mm-yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public void setValue(ASTDateType ast){
        this.ast = ast;
    }

    public ASTDateType getValue(){
        return ast;
    }

    public Optional<Date> resolve(){
        try{
            Date date = dateFormat.parse(String.format("%2d-%2d-%4d", ast.getDay().getValue(), ast.getMonth().getValue(), ast.getYear().getValue()));
            return Optional.of(date);
        }catch(ParseException e){
            return Optional.empty();
        }
    }
}
