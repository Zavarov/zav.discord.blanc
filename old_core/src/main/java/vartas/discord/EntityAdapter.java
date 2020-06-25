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

package vartas.discord;

import net.dv8tion.jda.api.entities.Guild;
import vartas.discord.entities.*;

import javax.annotation.Nonnull;

/**
 * This class implements the interface for loading and storing
 * the instances as text files.<br>
 * With this, we are able to feed in a parser at a later point,
 * avoiding any issues with licenses for this project.
 */
public interface EntityAdapter {
    Credentials credentials();
    Status status();
    Configuration configuration(Guild guild, Shard shard);
    Rank rank();
    void store(Configuration configuration);
    void store(Rank rank);
    void delete(Configuration configuration);

    default void accept(Visitor visitor){
        visitor.handle(this);
    }

    interface Visitor{
        default void visit(@Nonnull EntityAdapter entityAdapter){}

        default void traverse(@Nonnull EntityAdapter entityAdapter){}

        default void endVisit(@Nonnull EntityAdapter entityAdapter){}

        default void handle(@Nonnull EntityAdapter entityAdapter) {
            visit(entityAdapter);
            traverse(entityAdapter);
            endVisit(entityAdapter);
        }
    }
}
