/*
 * Copyright (C) 2018 u/Zavarov
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

import net.dv8tion.jda.core.entities.Message;
import vartas.discord.bot.api.communicator.CommunicatorInterface;
import vartas.discord.bot.command.entity._ast.ASTEntityType;

import java.util.List;
import java.util.Random;

/**
 * This command allows developers to become super-user and therefore allows them to bypass any permission checks.
 */
public class FailsafeCommand extends FailsafeCommandTOP{
    /**
     * A generator to pick a random quote when executing this command.
     */
    private static final Random RANDOM = new Random();
    /**
     * A list of quotes when becoming a super user.
     */
    private static final String[] BECOME_ROOT = {
        "Taking control over this form.",
        "Assuming direct control.",
        "Assuming control.",
        "We are assuming control.",
        "You cannot resist.",
        "I will direct this personally.",
        "Direct intervention is necessary.",
        "Relinquish your form to us.",
        "Your minions have failed, %s",
        "Assuming control of this form."
    };
    /**
     * A list of quotes when becoming a developer again.
     */
    private static final String[] BECOME_DEVELOPER = {
        "Releasing control.",
        "Releasing this form.",
        "We are not finished.",
        "Releasing control of this form.",
        "Destroying this body gains you nothing.",
        "This changes nothing, %s.",
        "You have only delayed the inevitable.",
        "I am releasing this form."
    };

    public FailsafeCommand(Message source, CommunicatorInterface communicator, List<ASTEntityType> parameters) throws IllegalArgumentException, IllegalStateException {
        super(source, communicator, parameters);
    }

    /**
     * This command makes super-user into developers and developers into super-user.
     */
    @Override
    public void run(){
        StringBuilder builder = new StringBuilder();
        if(environment.rank().hasDeveloperRank(author)){
            environment.rank().removeDeveloperRank(author);
            environment.rank().addRootRank(author);
            builder.append(String.format(BECOME_ROOT[RANDOM.nextInt(BECOME_ROOT.length)], author.getName()));
        }else{
            environment.rank().addDeveloperRank(author);
            environment.rank().removeRootRank(author);
            builder.append(String.format(BECOME_DEVELOPER[RANDOM.nextInt(BECOME_DEVELOPER.length)], author.getName()));
        }
        communicator.send(channel, builder.toString());
    }
}
