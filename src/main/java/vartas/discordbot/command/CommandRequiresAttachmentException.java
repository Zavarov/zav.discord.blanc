/*
 * Copyright (C) 2017 u/Zavarov
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

package vartas.discordbot.command;

/**
 * This exception is thrown when a command requires a message with at least one
 * attachment, but it hasn't had any.
 * @author u/Zavarov
 */
public class CommandRequiresAttachmentException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public CommandRequiresAttachmentException(){
        super("This command requires a file attached to the message that called it.");
    }
}
