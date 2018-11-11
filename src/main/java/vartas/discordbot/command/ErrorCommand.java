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
package vartas.discordbot.command;

import java.util.Arrays;
import vartas.discordbot.messages.InteractiveMessage;

/**
 * This class is used to print the error message that was caused by an exception.
 * @author u/Zavarov
 */
public class ErrorCommand extends Command{
    /**
     * The exception that caused an error.
     */
    protected Exception exception;
    /**
     * @param exception the exception that caused the error.
     */
    public ErrorCommand(Exception exception){
        this.exception = exception;
    }
    /**
     * Fills an interactive message with the stack trace and sends it.
     */
    @Override
    protected void execute(){
        InteractiveMessage.Builder error = new InteractiveMessage.Builder(
                message.getChannel(),
                message.getAuthor(),
                comm
        );
        error.addLine(exception.toString());
        error.addLines(Arrays.asList(exception.getStackTrace()), 10);
        log.error(String.format("%s",exception.toString()));
        comm.send(error.build());
    }
}
