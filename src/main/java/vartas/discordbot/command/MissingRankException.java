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

/**
 * This exception is thrown when a command with insufficient rank was executed.
 * @author u/Zavarov
 */
public class MissingRankException extends RuntimeException{
    private static final long serialVersionUID = 1L;
    public MissingRankException(Rank rank){
        super(String.format("You need to have %s or any higher rank to execute this command.",rank.toString()));
    }
}
