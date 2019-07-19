package vartas.discord.bot.command.parameter._symboltable;

import net.dv8tion.jda.core.OnlineStatus;
import vartas.discord.bot.command.entity._ast.ASTOnlineStatusType;

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
public class OnlineStatusSymbol extends OnlineStatusSymbolTOP {
    protected ASTOnlineStatusType ast;

    public OnlineStatusSymbol(String name) {
        super(name);
    }

    public void setValue(ASTOnlineStatusType ast){
        this.ast = ast;
    }

    public ASTOnlineStatusType getValue(){
        return ast;
    }

    public Optional<OnlineStatus> resolve(){
        switch(ast.getStatus()){
            case "online":
                return Optional.of(OnlineStatus.ONLINE);
            case "invisible":
                return Optional.of(OnlineStatus.INVISIBLE);
            case "busy":
                return Optional.of(OnlineStatus.DO_NOT_DISTURB);
            case "idle":
                return Optional.of(OnlineStatus.IDLE);
            default:
                return Optional.empty();
        }
    }
}
