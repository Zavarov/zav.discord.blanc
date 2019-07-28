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

package vartas.discord.bot.command.parameter._symboltable;

import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.requests.ErrorResponse;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;

public class MessageSymbol extends MessageSymbolTOP{
    protected Supplier<BigDecimal> id;

    public MessageSymbol(String name) {
        super(name);
    }

    public void setValue(Supplier<BigDecimal> id){
        this.id = id;
    }

    public void setValue(BigDecimal id){
        this.id = () -> id;
    }

    public String getQualifiedResolvedName(){
        return Message.class.getCanonicalName();
    }

    public Optional<Message> resolve(Message context){
        checkNotNull(context);
        checkNotNull(context.getTextChannel());

        try {
            return Optional.of(context.getTextChannel().getMessageById(id.get().longValueExact()).complete());
        }catch(ErrorResponseException e){
            //The message id was invalid
            if(e.getErrorResponse() == ErrorResponse.UNKNOWN_MESSAGE)
                return Optional.empty();
            throw e;
        }
    }
}
