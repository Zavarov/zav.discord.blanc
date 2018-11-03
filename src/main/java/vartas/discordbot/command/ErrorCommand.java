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

import java.io.IOException;
import vartas.discordbot.messages.InteractiveMessage;

/**
 *
 * @author u/Zavarov
 */
public class ErrorCommand extends Command{
    protected Exception exception;
    public ErrorCommand(Exception exception){
        this.exception = exception;
    }
    @Override
    protected void execute() throws IOException, InterruptedException {
        InteractiveMessage.Builder error = new InteractiveMessage.Builder(
                message.getChannel(),
                message.getAuthor()
        );
        error.addLine(exception.toString());
        StackTraceElement[] stack_trace = exception.getStackTrace();
        for(int i = 0 ; i < stack_trace.length ; i+=10){
          for(int j = i ; j < Math.min(i+10,stack_trace.length) ; ++j){
              error.addLine(stack_trace[j].toString());
          }
          error.nextPage();
        }
        log.error(String.format("%s",exception.toString()));
        error.build().send(interactives::add);
    }
}
