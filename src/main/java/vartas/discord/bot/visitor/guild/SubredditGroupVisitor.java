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

package vartas.discord.bot.visitor.guild;

import net.dv8tion.jda.api.entities.TextChannel;
import vartas.discord.bot.entities.guild.SubredditGroup;

import java.util.Set;

public interface SubredditGroupVisitor {
    default void visit(SubredditGroup group){}

    default void traverse(SubredditGroup group) {
        group.accept(this);
    }

    default void handle(SubredditGroup group){
        visit(group);
        traverse(group);
    }

    default void visitChannels(String key, Set<TextChannel> values){}

    default void traverseChannels(String key, Set<TextChannel> values){
        for(TextChannel value : values)
            handle(key, value);
    }

    default void handleChannels(String key, Set<TextChannel> values){
        visitChannels(key, values);
        traverseChannels(key, values);
    }

    default void visit(String key, TextChannel value){}

    default void traverse(String key, TextChannel value){}

    default void handle(String key, TextChannel value){
        visit(key, value);
        traverse(key, value);
    }
}
