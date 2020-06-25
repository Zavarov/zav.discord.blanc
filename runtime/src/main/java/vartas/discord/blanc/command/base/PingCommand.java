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

package vartas.discord.blanc.command.base;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This command measures the time it takes to send a command and receiving
 * the confirmation from Discord.
 */
public class PingCommand extends PingCommandTOP{
    private static final List<String> responses = Arrays.asList(
            "Tch.",
            "Are you worried about me?",
            "Hm... What's this?",
            "That's it?",
            "Another one?",
            "I feel great!"
    );

    @Override
    public void run() {
        int index = ThreadLocalRandom.current().nextInt(responses.size());
        get$MessageChannel().send(responses.get(index));
    }
}
