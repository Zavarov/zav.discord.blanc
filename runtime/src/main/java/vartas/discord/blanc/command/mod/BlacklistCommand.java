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

package vartas.discord.blanc.command.mod;

import vartas.discord.blanc.Shard;
import vartas.discord.blanc.io.json.JSONCredentials;
import vartas.discord.blanc.json.JSONGuild;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * This command allows to blacklist certain words. Any message that contains the
 * word will be deleted by the bot.
 */
public class BlacklistCommand extends BlacklistCommandTOP{
    @Override
    public void run(){
        if(get$Guild().containsBlacklist(getExpression())){
            get$Guild().removeBlacklist(getExpression());
            get$TextChannel().send("Removed '"+getExpression()+"' from the blacklist.");
        }else{
            try{
                //Check if the regex is valid
                Pattern.compile(getExpression());

                get$Guild().addBlacklist(getExpression());
                get$TextChannel().send("Blacklisted '"+getExpression()+"'.");

                Shard.write(get$Guild());
                get$Guild().compilePattern();
            }catch(PatternSyntaxException e){
                get$TextChannel().send(e);
            }
        }
    }
}
