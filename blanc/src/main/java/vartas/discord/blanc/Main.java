package vartas.discord.blanc;

import de.monticore.symboltable.GlobalScope;
import vartas.discord.blanc.command.CommandBuilder;
import vartas.discord.bot.api.environment.DiscordEnvironment;

import javax.security.auth.login.LoginException;
import java.io.File;

import static vartas.discord.bot.command.Main.createGlobalScope;
import static vartas.discord.bot.command.Main.parseModels;

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
public class Main {
    public static void main(String[] args) throws InterruptedException, LoginException {
        GlobalScope scope = createGlobalScope();
        parseModels(new File("models"),scope);

        new DiscordEnvironment(scope, CommandBuilder::new);
    }
}
