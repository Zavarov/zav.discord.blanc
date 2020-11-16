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

package vartas.discord.blanc.command.base;

import vartas.discord.blanc.io.$json.JSONCredentials;

/**
 * This command prints an invitation link to the support server.
 */
public class SupportCommand extends SupportCommandTOP{
    @Override
    public void run(){
        StringBuilder stringBuilder = new StringBuilder()
                .append("If you have any questions, hit me up in the support server:\n")
                .append(JSONCredentials.CREDENTIALS.getInviteSupportServer());
        get$MessageChannel().send(stringBuilder);
    }
}
