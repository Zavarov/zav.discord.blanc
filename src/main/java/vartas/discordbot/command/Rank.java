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
 * Some commands are restricted to only those people who have the rank to use
 * them.
 * @author u/Zavarov
 */
public enum Rank {
    ROOT("Root"),
    DEVELOPER("Developer"),
    REDDIT("Reddit"),
    USER("User");
    
    /**
     * The plainttext of the rank.
     */
    private final String plaintext;
    /**
     * @param plaintext the plaintext of the rank. 
     */
    private Rank(String plaintext){
        this.plaintext = plaintext;
    }
    @Override
    public String toString(){
        return plaintext;
    }
}
