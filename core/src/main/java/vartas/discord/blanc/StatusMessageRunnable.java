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

package vartas.discord.blanc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vartas.discord.blanc.io.$json.JSONStatusMessages;

import javax.annotation.Nonnull;
import java.util.Random;

public class StatusMessageRunnable implements Runnable{
    @Nonnull
    private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());
    @Nonnull
    private final SelfUser selfUser;
    @Nonnull
    private final Random random = new Random();

    public StatusMessageRunnable(@Nonnull SelfUser selfUser){
        this.selfUser = selfUser;
    }

    @Override
    public void run() {
        int index = random.nextInt(JSONStatusMessages.STATUS_MESSAGES.sizeStatusMessages());
        String statusMessage = JSONStatusMessages.STATUS_MESSAGES.getStatusMessages(index);
        selfUser.modifyStatusMessage(statusMessage);
        log.trace("Changing status message to '{}'.", statusMessage);
    }
}
